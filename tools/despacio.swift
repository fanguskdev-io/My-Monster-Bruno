// tools/despacio.swift · alarga voces sin cambiarles el tono, para tools/voces.py (ADR 0004).
//
//     swiftc -O tools/despacio.swift -o despacio
//     ./despacio 0.75 entrada.wav salida.wav [entrada2.wav salida2.wav ...]
//
// El primer número es la velocidad: 0,75 quiere decir que la voz dura un tercio más y se entiende mejor a los 4 años. Usa
// AVAudioUnitTimePitch de Apple, que cambia el tempo sin tocar el tono, en una cadena de audio que se procesa sin sonar
// (renderizado manual, fuera de línea). Escribe WAV de 16 bits a la frecuencia y con los canales de la entrada.

import AVFoundation

/// The tail rendered after the input's own length, for what the time-pitch unit still holds.
let tailSeconds = 0.3

func slowDown(_ input: URL, to output: URL, rate: Float) throws {
    let source = try AVAudioFile(forReading: input)
    let format = source.processingFormat
    let engine = AVAudioEngine()
    let player = AVAudioPlayerNode()
    let timePitch = AVAudioUnitTimePitch()
    timePitch.rate = rate
    engine.attach(player)
    engine.attach(timePitch)
    engine.connect(player, to: timePitch, format: format)
    engine.connect(timePitch, to: engine.mainMixerNode, format: format)
    try engine.enableManualRenderingMode(.offline, format: format, maximumFrameCount: 4096)
    try engine.start()
    player.scheduleFile(source, at: nil)
    player.play()

    let settings: [String: Any] = [
        AVFormatIDKey: kAudioFormatLinearPCM,
        AVSampleRateKey: format.sampleRate,
        AVNumberOfChannelsKey: format.channelCount,
        AVLinearPCMBitDepthKey: 16,
        AVLinearPCMIsFloatKey: false,
        AVLinearPCMIsBigEndianKey: false,
    ]
    let target = try AVAudioFile(
        forWriting: output, settings: settings, commonFormat: format.commonFormat, interleaved: format.isInterleaved)
    guard let buffer = AVAudioPCMBuffer(
        pcmFormat: engine.manualRenderingFormat, frameCapacity: engine.manualRenderingMaximumFrameCount)
    else { throw NSError(domain: "despacio", code: 1) }
    let total = AVAudioFramePosition(Double(source.length) / Double(rate) + tailSeconds * format.sampleRate)
    while engine.manualRenderingSampleTime < total {
        let left = AVAudioFrameCount(total - engine.manualRenderingSampleTime)
        switch try engine.renderOffline(min(buffer.frameCapacity, left), to: buffer) {
        case .success: try target.write(from: buffer)
        case .error: throw NSError(domain: "despacio", code: 2)
        default: break
        }
    }
    player.stop()
    engine.stop()
}

let arguments = Array(CommandLine.arguments.dropFirst())
guard let rate = Float(arguments.first ?? ""), rate > 0, arguments.count >= 3, arguments.count % 2 == 1 else {
    FileHandle.standardError.write("uso: despacio <velocidad> <entrada.wav> <salida.wav> [...]\n".data(using: .utf8)!)
    exit(2)
}
for pair in stride(from: 1, to: arguments.count, by: 2) {
    try slowDown(URL(fileURLWithPath: arguments[pair]), to: URL(fileURLWithPath: arguments[pair + 1]), rate: rate)
}

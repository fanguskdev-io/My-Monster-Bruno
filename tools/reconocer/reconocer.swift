// tools/reconocer/reconocer.swift · el reconocedor de voz de la versión del Mac, para «Habla con Bruno» (ADR 0027).
//
//     ./reconocer --permiso          pide el permiso la primera vez; sale con 0 si puede reconocer en el propio Mac
//     ./reconocer voz.wav palabra    escribe lo que ha entendido, una apuesta por línea, la mejor primero
//
// Usa el reconocimiento de voz de Apple para el español de España en el propio Mac, nunca por internet, como el juego en
// el iPhone (ADR 0015). Lo compila Gradle al abrir la versión de escritorio (desktopApp:run), con su Info.plist dentro
// del binario, que macOS exige para pedir el permiso. Sale con 2 si no tiene permiso y con 1 si no ha podido reconocer.
//
// macOS pide el permiso en nombre de quien lanzó el programa (el terminal, el juego...), y si ese no explica para qué lo
// quiere, cierra el programa en vez de preguntar. Por eso se vuelve a lanzar a sí mismo como responsable de su permiso,
// como hace el `disclaim` de Qt Creator: así lo pide en su nombre, con su Info.plist.

import Darwin
import Foundation
import Speech

/// Marks the run that is responsible for itself, so that it does not launch itself again.
let aloneMark = "BRUNO_RECONOCER_SOLO"

/// macOS's own call for a process that answers for itself before the privacy checks (spawn_private.h).
@_silgen_name("responsibility_spawnattrs_setdisclaim")
func setDisclaim(_ attributes: UnsafeMutablePointer<posix_spawnattr_t?>, _ disclaim: Int32) -> Int32

/// The run started by onItsOwn, to stop it too if this one is stopped.
var started: pid_t = 0

/// Runs this same program again, responsible for itself and with the same arguments, and returns how it ended.
func onItsOwn() -> Int32 {
    guard let path = Bundle.main.executablePath else { return 1 }
    var attributes: posix_spawnattr_t?
    posix_spawnattr_init(&attributes)
    defer { posix_spawnattr_destroy(&attributes) }
    _ = setDisclaim(&attributes, 1)
    var environment = ProcessInfo.processInfo.environment
    environment[aloneMark] = "1"
    let argv: [UnsafeMutablePointer<CChar>?] = CommandLine.arguments.map { strdup($0) } + [nil]
    let envp: [UnsafeMutablePointer<CChar>?] = environment.map { strdup("\($0.key)=\($0.value)") } + [nil]
    defer { (argv + envp).forEach { free($0) } }
    signal(SIGTERM) { _ in
        if started > 0 { kill(started, SIGTERM) }
        _exit(143)
    }
    guard posix_spawn(&started, path, nil, &attributes, argv, envp) == 0 else { return 1 }
    var status: Int32 = 0
    while waitpid(started, &status, 0) == -1 && errno == EINTR {}
    let killedBy = status & 0x7f
    return killedBy == 0 ? (status >> 8) & 0xff : 128 + killedBy
}

/// Spanish of Spain, as she speaks it.
let language = "es-ES"

/// How long the recognizer may take with a word: she is waiting for Bruno.
let recognizeSeconds = 5.0

/// How long it waits for a grown-up to answer macOS's dialog, the first time.
let permissionSeconds = 300.0

/// An answer that comes on another thread.
final class Answer<T>: @unchecked Sendable {
    private let lock = NSLock()
    private var value: T?

    func give(_ answer: T) {
        lock.lock()
        defer { lock.unlock() }
        if value == nil { value = answer }
    }

    var given: T? {
        lock.lock()
        defer { lock.unlock() }
        return value
    }
}

/// Runs the main run loop until [answer] is given or [seconds] pass: the recognizer and its permission answer through it.
func wait<T>(for answer: Answer<T>, seconds: Double) -> T? {
    let deadline = Date().addingTimeInterval(seconds)
    while answer.given == nil && Date() < deadline {
        RunLoop.main.run(mode: .default, before: Date().addingTimeInterval(0.05))
    }
    return answer.given
}

/// The recognizer, if it can work on the Mac itself right now.
func onDevice() -> SFSpeechRecognizer? {
    guard let recognizer = SFSpeechRecognizer(locale: Locale(identifier: language)),
          recognizer.supportsOnDeviceRecognition, recognizer.isAvailable else { return nil }
    return recognizer
}

/// Whether it may recognize: the first time, it asks with macOS's dialog and waits for the answer.
func allowed() -> Bool {
    if SFSpeechRecognizer.authorizationStatus() == .notDetermined {
        let answer = Answer<SFSpeechRecognizerAuthorizationStatus>()
        SFSpeechRecognizer.requestAuthorization { answer.give($0) }
        _ = wait(for: answer, seconds: permissionSeconds)
    }
    return SFSpeechRecognizer.authorizationStatus() == .authorized
}

/// What [recognizer] understood in the WAV at [path], best first, with [word] as a hint; empty if it could not tell.
func heard(_ path: String, hint word: String, with recognizer: SFSpeechRecognizer) -> [String] {
    let request = SFSpeechURLRecognitionRequest(url: URL(fileURLWithPath: path))
    request.requiresOnDeviceRecognition = true
    request.shouldReportPartialResults = false
    request.contextualStrings = [word]
    let answer = Answer<[String]>()
    let task = recognizer.recognitionTask(with: request) { result, error in
        if let result, result.isFinal {
            answer.give(result.transcriptions.map(\.formattedString))
        } else if error != nil {
            answer.give([])
        }
    }
    let guesses = wait(for: answer, seconds: recognizeSeconds) ?? []
    task.cancel()
    return guesses
}

if ProcessInfo.processInfo.environment[aloneMark] == nil {
    exit(onItsOwn())
}
let arguments = Array(CommandLine.arguments.dropFirst())
if arguments == ["--permiso"] {
    exit(allowed() && onDevice() != nil ? 0 : 1)
}
guard arguments.count == 2 else {
    FileHandle.standardError.write(Data("uso: reconocer --permiso | reconocer voz.wav palabra\n".utf8))
    exit(64)
}
guard SFSpeechRecognizer.authorizationStatus() == .authorized else { exit(2) }
guard let recognizer = onDevice() else { exit(1) }
recognizer.queue = OperationQueue()
let guesses = heard(arguments[0], hint: arguments[1], with: recognizer)
if guesses.isEmpty { exit(1) }
print(guesses.joined(separator: "\n"))
exit(0)

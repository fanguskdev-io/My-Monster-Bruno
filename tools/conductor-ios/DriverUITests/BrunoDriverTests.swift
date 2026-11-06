import XCTest

/// Drives MyMonsterBruno on the simulator by its bundle identifier: taps on its 800 x 400 stage, turns the phone both
/// ways and, at each checkpoint, waits for the host to take a masked screenshot (`simctl io screenshot --mask=black`),
/// which `vigilante.sh` does. How to run it, and how to start from a save, in docs/pruebas.md («Recorridos en el iPhone»).
final class BrunoDriverTests: XCTestCase {
    private let app = XCUIApplication(bundleIdentifier: "com.fanguskdev.mymonsterbruno.MyMonsterBruno")
    private var sync = URL(fileURLWithPath: "/tmp/bruno-conductor/sync")
    private var log: [String] = []

    override func setUp() {
        continueAfterFailure = true
        if let dir = ProcessInfo.processInfo.environment["CONDUCTOR_DIR"] { sync = URL(fileURLWithPath: dir) }
        app.launchArguments = ["-silencio"]
    }

    override func tearDown() {
        app.terminate()
        try? log.joined(separator: "\n").write(to: sync.appendingPathComponent("log.txt"), atomically: true, encoding: .utf8)
    }

    private var screen: CGSize {
        let f = app.frame
        return CGSize(width: max(f.width, f.height), height: min(f.width, f.height))
    }

    private var scale: CGFloat { min(screen.width / 800, screen.height / 400) }

    private func checkpoint(_ name: String) {
        sleep(2)
        log.append("\(name): frame \(app.frame), orientation \(XCUIDevice.shared.orientation.rawValue)")
        let ready = sync.appendingPathComponent("\(name).ready")
        let done = sync.appendingPathComponent("\(name).done")
        try? "".write(to: ready, atomically: true, encoding: .utf8)
        let deadline = Date().addingTimeInterval(30)
        while !FileManager.default.fileExists(atPath: done.path) && Date() < deadline { usleep(200_000) }
    }

    /// A point of the screen, in points from its top left corner, as the game is seen.
    private func at(_ x: CGFloat, _ y: CGFloat) -> XCUICoordinate {
        app.coordinate(withNormalizedOffset: CGVector(dx: x / screen.width, dy: y / screen.height))
    }

    /// A point of the stage.
    private func stage(_ x: CGFloat, _ y: CGFloat) -> XCUICoordinate {
        let dx = (screen.width - 800 * scale) / 2, dy = (screen.height - 400 * scale) / 2
        return at(dx + x * scale, dy + y * scale)
    }

    private var house: XCUICoordinate { at(44 * scale, 42 * scale) }
    private var lock: XCUICoordinate { at(screen.width - 38 * scale, 38 * scale) }

    /// Opens the game and goes past its start screen (ADR 0018), which a touch anywhere leaves, after [name] if given.
    private func launchPastStart(_ name: String? = nil) {
        app.launch()
        sleep(3)
        if let name { checkpoint(name) }
        stage(400, 200).tap()
        sleep(2)
    }

    func testWalkThroughHeldBothWays() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart("0-inicio-izquierda")
        checkpoint("1-nacer-izquierda")
        for _ in 0..<5 {
            stage(400, 236).tap()
            usleep(500_000)
        }
        sleep(3)
        stage(88, 132).tap()
        sleep(2)
        stage(712, 96).tap()
        sleep(2)
        checkpoint("2-elegir-izquierda")
        stage(566, 330).tap()
        sleep(3)
        checkpoint("3-cuarto-izquierda")
        stage(680, 290).tap()
        sleep(3)
        checkpoint("4-mapa-izquierda")
        stage(262, 258).tap()
        sleep(5)
        checkpoint("5-comeletras-izquierda")

        XCUIDevice.shared.orientation = .landscapeRight
        sleep(2)
        checkpoint("5-comeletras-derecha")
        house.tap()
        sleep(3)
        checkpoint("4-mapa-derecha")
        house.tap()
        sleep(3)
        checkpoint("3-cuarto-derecha")
        stage(82, 318).tap()
        sleep(3)
        checkpoint("6-calma-derecha")
        house.tap()
        sleep(3)
        lock.press(forDuration: 3.5)
        sleep(2)
        checkpoint("7-mayores-derecha")
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(2)
        checkpoint("7-mayores-izquierda")
    }
    /// The final version, M2 and M4 together: every island, «Habla con Bruno», the calm corner and the grown-ups' corner,
    /// with the island on the left and then on the right.
    func testFinalVersionBothWays() {
        let islands: [(String, CGFloat, CGFloat)] = [
            ("05-comeletras", 262, 258), ("06-fresas", 588, 150), ("07-globos", 674, 318),
            ("08-casa", 340, 78), ("09-emociones", 112, 150),
        ]
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart("00-inicio-izq")
        checkpoint("01-nacer-izq")
        for _ in 0..<5 {
            stage(400, 236).tap()
            usleep(500_000)
        }
        sleep(3)
        stage(88, 132).tap()
        sleep(2)
        stage(712, 96).tap()
        sleep(2)
        stage(566, 330).tap()
        sleep(3)
        checkpoint("02-cuarto-izq")
        stage(82, 200).tap()
        sleep(4)
        checkpoint("03-habla-izq")
        house.tap()
        sleep(3)
        stage(680, 290).tap()
        sleep(3)
        checkpoint("04-mapa-izq")
        for (name, x, y) in islands {
            stage(x, y).tap()
            sleep(5)
            checkpoint("\(name)-izq")
            house.tap()
            sleep(3)
        }

        XCUIDevice.shared.orientation = .landscapeRight
        sleep(2)
        checkpoint("04-mapa-der")
        for (name, x, y) in islands.reversed() {
            stage(x, y).tap()
            sleep(5)
            checkpoint("\(name)-der")
            house.tap()
            sleep(3)
        }
        house.tap()
        sleep(3)
        checkpoint("02-cuarto-der")
        stage(82, 200).tap()
        sleep(4)
        checkpoint("03-habla-der")
        house.tap()
        sleep(3)
        stage(82, 318).tap()
        sleep(3)
        checkpoint("10-calma-der")
        house.tap()
        sleep(3)
        lock.press(forDuration: 3.5)
        sleep(2)
        checkpoint("11-mayores-der")
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(2)
        checkpoint("11-mayores-izq")
    }

    private func tapText(_ text: String) {
        let element = app.staticTexts[text]
        if element.waitForExistence(timeout: 4) {
            element.tap()
        } else {
            log.append("no encuentro «\(text)»")
        }
    }

    /// «Habla con Bruno» in silence (Bruno says the word himself) and her photo chosen with the system's picker.
    func testTalkAndPhoto() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        checkpoint("a1-cuarto-izq")
        stage(82, 200).tap()
        sleep(4)
        checkpoint("a2-habla-izq")
        stage(604, 322).press(forDuration: 1.0)
        sleep(2)
        checkpoint("a3-habla-palmadas-izq")
        XCUIDevice.shared.orientation = .landscapeRight
        sleep(5)
        checkpoint("a4-habla-der")
        house.tap()
        sleep(3)
        lock.press(forDuration: 3.5)
        sleep(2)
        checkpoint("a5-mayores-der")
        at(400, 330).press(forDuration: 0.2, thenDragTo: at(400, 60), withVelocity: 300, thenHoldForDuration: 0.3)
        sleep(1)
        checkpoint("a5b-mayores-abajo-der")
        tapText("Elegir su foto")
        sleep(5)
        checkpoint("a6-galeria-der")
        // The picker is Apple's own process: its first photo, by where it is.
        at(132, 298).tap()
        sleep(5)
        checkpoint("a7-mayores-con-foto-der")
        at(400, 60).press(forDuration: 0.2, thenDragTo: at(400, 360), withVelocity: 300, thenHoldForDuration: 0.3)
        sleep(1)
        tapText("Volver al juego")
        sleep(3)
        checkpoint("a8-cuarto-con-foto-der")
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(3)
        checkpoint("a9-cuarto-con-foto-izq")
    }

    /// The island of words, and the T, the N and the D (the save knows the vowels and the M, the P, the L and the S).
    func testWordsAndLetters() {
        XCUIDevice.shared.orientation = .landscapeRight
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        checkpoint("b1-mapa-der")
        stage(712, 56).tap()
        sleep(7)
        checkpoint("b2-palabras-der")
        stage(420, 330).tap()
        sleep(1)
        checkpoint("b3-palabras-carta-der")
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(6)
        checkpoint("b4-palabras-izq")
        house.tap()
        sleep(3)
        checkpoint("b5-mapa-izq")
        stage(262, 258).tap()
        sleep(6)
        checkpoint("b6-comeletras-izq")
        XCUIDevice.shared.orientation = .landscapeRight
        sleep(3)
        checkpoint("b7-comeletras-der")
    }

    /// The start screen and writing her name (the save has Bruno hatched and her name, Lucía, never written): Bruno asks for
    /// it, the bunting leads to it, and the L, traced in two drags (down, and from the corner to the right), flies up to its
    /// pennant and leaves the U.
    func testStartAndName() {
        XCUIDevice.shared.orientation = .landscapeLeft
        app.launch()
        checkpoint("c1-inicio-izq")
        XCUIDevice.shared.orientation = .landscapeRight
        checkpoint("c3-inicio-der")
        stage(400, 200).tap()
        sleep(1)
        checkpoint("c4-cuarto-banderin-der")
        stage(520, 70).tap()
        sleep(3)
        checkpoint("c5-nombre-der")
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(2)
        checkpoint("c6-nombre-izq")
        stage(389, 168).press(forDuration: 0.2, thenDragTo: stage(389, 354), withVelocity: 200, thenHoldForDuration: 0.1)
        sleep(1)
        checkpoint("c7-nombre-palo-izq")
        stage(389, 356).press(forDuration: 0.2, thenDragTo: stage(478, 356), withVelocity: 200, thenHoldForDuration: 0.1)
        sleep(1)
        checkpoint("c9-nombre-u-izq")
        house.tap()
        sleep(2)
        checkpoint("c10-cuarto-izq")
    }

    /// The feeling of the day (the save has Bruno hatched and no feeling asked today): Bruno asks, a jar says its name,
    /// her photo (or the face) goes to the jar of sadness, Bruno answers and puts his own in the jar of joy, and the room.
    func testFeelingOfTheDay() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        checkpoint("f1-emocion-izq")
        stage(424, 260).tap()
        checkpoint("f2-emocion-bote-izq")
        XCUIDevice.shared.orientation = .landscapeRight
        sleep(2)
        checkpoint("f3-emocion-der")
        stage(530, 106).press(forDuration: 0.3, thenDragTo: stage(424, 262), withVelocity: 300, thenHoldForDuration: 0.2)
        usleep(800_000)
        checkpoint("f4-emocion-tristeza-der")
        sleep(8)
        checkpoint("f5-cuarto-der")
    }

    /// The island of words to its end, the rhymes included (ADR 0021): it touches the three cards in turn, as a child
    /// trying them would, with a checkpoint after each turn, and turns the phone halfway. The save has three cards to
    /// choose from and the feeling of today already asked.
    func testRhymes() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        stage(712, 56).tap()
        sleep(8)
        // About four turns for the words that start the same, six for the rhymes and one to the present.
        for turn in 1...11 {
            for x: CGFloat in [420, 570, 720] {
                stage(x, 330).tap()
                sleep(4)
            }
            checkpoint(String(format: "r%02d", turn))
            if turn == 7 { XCUIDevice.shared.orientation = .landscapeRight }
        }
    }

    /// The countdown of the island of numbers (ADR 0023): the strawberries first, touched in turn until they are over, and
    /// then the panel, its ten buttons in turn until every light is on, and the red zero. The save has the feeling of
    /// today already asked.
    func testCountdown() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        stage(588, 150).tap()
        sleep(8)
        let berries: [(CGFloat, CGFloat)] = [
            (528, 200), (588, 180), (648, 180), (706, 200), (512, 254), (572, 238),
            (632, 238), (692, 254), (538, 304), (598, 292), (658, 292), (716, 306),
        ]
        for _ in 1...6 {
            for (x, y) in berries {
                stage(x, y).tap()
                usleep(1_200_000)
            }
        }
        checkpoint("n1-cohete-izq")
        let buttons: [(CGFloat, CGFloat)] = [CGFloat(175), 255].flatMap { y in [CGFloat(455), 530, 605, 680, 755].map { x in (x, y) } }
        for turn in 1...10 {
            for (x, y) in buttons {
                stage(x, y).tap()
                usleep(900_000)
            }
            if turn == 3 {
                checkpoint("n2-contando-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("n3-cero-der")
        stage(605, 345).tap()
        usleep(1_500_000)
        checkpoint("n4-despegue-der")
        sleep(8)
        checkpoint("n5-regalo-der")
    }
}

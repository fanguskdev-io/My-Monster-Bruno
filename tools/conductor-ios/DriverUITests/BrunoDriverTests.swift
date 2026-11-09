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

    /// Drags the grown-ups' corner slowly, down or up, until [text] can be touched: a quick swipe does not move it.
    private func scrollTo(_ text: String, down: Bool = true) {
        let element = app.staticTexts[text]
        for _ in 0..<10 {
            if element.exists && element.isHittable { return }
            if down {
                at(400, 330).press(forDuration: 0.2, thenDragTo: at(400, 60), withVelocity: 300, thenHoldForDuration: 0.3)
            } else {
                at(400, 60).press(forDuration: 0.2, thenDragTo: at(400, 360), withVelocity: 300, thenHoldForDuration: 0.3)
            }
            sleep(1)
        }
        log.append("no llego a «\(text)»")
    }

    /// Chooses a photo in Apple's picker, by where it is, [x] and [y] in points. The picker is another process, and slow
    /// to close with a big photo when the computer is busy: it waits until the grown-ups' corner can be touched again,
    /// with «Quitar su foto», and touches the photo again only if the picker is still there after a long while. When the
    /// photo is being changed, «Quitar su foto» is already there under the picker and says nothing: it waits [atLeast].
    private func pickPhoto(_ x: CGFloat, _ y: CGFloat, atLeast: UInt32 = 6) {
        let remove = app.staticTexts["Quitar su foto"]
        for _ in 0..<3 {
            at(x, y).tap()
            sleep(atLeast)
            let deadline = Date().addingTimeInterval(24)
            while Date() < deadline {
                if remove.exists && remove.isHittable { return }
                sleep(1)
            }
        }
        log.append("no encuentro que el selector eligiera la foto")
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
        pickPhoto(132, 280)
        checkpoint("a7-mayores-con-foto-der")
        scrollTo("Volver al juego", down: false)
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
        // The assembly (ADR 0030): today's card on the calendar, Monday first, and then the rain out of the window.
        checkpoint("f5-asamblea-der")
        let weekday = Calendar(identifier: .gregorian).component(.weekday, from: Date())
        stage(331 + 69 * CGFloat((weekday + 5) % 7), 112).tap()
        sleep(8)
        checkpoint("f6-tiempo-der")
        stage(576, 302).tap()
        sleep(8)
        checkpoint("f7-cuarto-lluvia-der")
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

    /// Her photo from beginning to end (ADR 0016): chosen in the grown-ups' corner, Bruno sees it in the room, she touches
    /// it, a grown-up changes it for another, and «Volver a empezar» takes Bruno back to the egg and keeps the photo, in its
    /// frame once he hatches again. The save has Bruno hatched, her name written and the feeling of today.
    func testPhotoAndRestart() {
        XCUIDevice.shared.orientation = .landscapeRight
        launchPastStart()
        sleep(2)
        lock.press(forDuration: 3.5)
        sleep(2)
        scrollTo("Elegir su foto")
        tapText("Elegir su foto")
        sleep(5)
        checkpoint("p1-galeria-der")
        // The picker is Apple's own process: its first photo, by where it is, high in its row: lower down it can have its
        // «Ubicación incluida» over the photos, which opens its options instead.
        pickPhoto(132, 280)
        scrollTo("Volver al juego", down: false)
        tapText("Volver al juego")
        sleep(4)
        checkpoint("p2-cuarto-eres-tu-der")
        stage(712, 152).tap()
        usleep(800_000)
        checkpoint("p3-toca-la-foto-der")
        lock.press(forDuration: 3.5)
        sleep(2)
        scrollTo("Cambiar su foto")
        tapText("Cambiar su foto")
        sleep(5)
        checkpoint("p4-galeria-otra-der")
        // Another photo, the second one of the row.
        pickPhoto(279, 280, atLeast: 12)
        checkpoint("p5-mayores-otra-foto-der")
        scrollTo("Volver a empezar")
        tapText("Volver a empezar")
        sleep(1)
        tapText("Toca otra vez para confirmar")
        sleep(3)
        // Back to the egg; and once Bruno hatches again, her photo is still in its frame.
        checkpoint("p6-huevo-der")
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
        sleep(4)
        checkpoint("p7-cuarto-con-la-foto-der")
    }

    /// Her photo in more places (ADR 0034): chosen in the grown-ups' corner, she sails in Bruno's boat on the map; when
    /// the minutes of the day run out there, her photo is pinned on the mission card at bedtime; and, the game opened
    /// again, she is in the boat of the start screen. The save has Bruno hatched, the feeling of today and forty seconds
    /// of play left.
    func testPhotoPlaces() {
        XCUIDevice.shared.orientation = .landscapeRight
        launchPastStart()
        sleep(1)
        lock.press(forDuration: 3.5)
        sleep(2)
        scrollTo("Elegir su foto")
        tapText("Elegir su foto")
        sleep(5)
        pickPhoto(132, 280)
        scrollTo("Volver al juego", down: false)
        tapText("Volver al juego")
        sleep(3)
        stage(680, 290).tap()
        sleep(3)
        checkpoint("f1-mapa-con-foto-der")
        sleep(40)
        checkpoint("f2-dormir-con-foto-der")
        app.terminate()
        app.launch()
        sleep(3)
        checkpoint("f3-inicio-con-foto-der")
    }

    /// The stickers of the room (ADR 0024): the save has every island finished and none stuck yet. She takes the first one
    /// from the rug to the wall, lets the next one go on the floor, where it does not stay, sticks it on the wall after
    /// all, and moves the first one somewhere else.
    func testStickers() {
        XCUIDevice.shared.orientation = .landscapeRight
        launchPastStart()
        sleep(3)
        checkpoint("s1-pegatina-der")
        stage(536, 354).press(forDuration: 0.3, thenDragTo: stage(150, 110), withVelocity: 400, thenHoldForDuration: 0.2)
        sleep(3)
        checkpoint("s2-pegada-der")
        stage(536, 354).press(forDuration: 0.3, thenDragTo: stage(200, 340), withVelocity: 400, thenHoldForDuration: 0.2)
        sleep(2)
        checkpoint("s3-vuelve-der")
        stage(536, 354).press(forDuration: 0.3, thenDragTo: stage(590, 180), withVelocity: 400, thenHoldForDuration: 0.2)
        sleep(3)
        stage(150, 110).press(forDuration: 0.3, thenDragTo: stage(60, 60), withVelocity: 400, thenHoldForDuration: 0.2)
        sleep(2)
        XCUIDevice.shared.orientation = .landscapeLeft
        sleep(2)
        checkpoint("s4-movida-izq")
    }

    /// Her favourite animal (ADR 0033): the cat a grown-up chose, which Bruno welcomes the first time, touched so that it
    /// jumps; then the dog chosen in the grown-ups' corner, and the room with it.
    func testPet() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(5)
        checkpoint("p1-cuarto-gato-izq")
        stage(554, 204).tap()
        usleep(400_000)
        checkpoint("p2-gato-salta-izq")
        sleep(3)
        XCUIDevice.shared.orientation = .landscapeRight
        sleep(2)
        lock.press(forDuration: 3.5)
        sleep(2)
        scrollTo("Perro")
        tapText("Perro")
        sleep(1)
        checkpoint("p3-mayores-perro-der")
        scrollTo("Volver al juego", down: false)
        tapText("Volver al juego")
        sleep(5)
        checkpoint("p4-cuarto-perro-der")
    }

    /// Bruno's wardrobe (ADR 0032), with three presents: from the hanger of the room, the beanie taken off, the glasses
    /// put on again, his colour and his head changed, and back to the room, where he stays so.
    func testWardrobe() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(198, 344).tap()
        sleep(4)
        checkpoint("w1-armario-izq")
        stage(532, 128).tap()
        sleep(2)
        stage(620, 128).tap()
        sleep(2)
        stage(620, 128).tap()
        sleep(2)
        stage(716, 274).tap()
        sleep(2)
        stage(620, 336).tap()
        sleep(2)
        checkpoint("w2-vestido-izq")
        XCUIDevice.shared.orientation = .landscapeRight
        sleep(2)
        checkpoint("w3-armario-der")
        stage(44, 42).tap()
        sleep(4)
        checkpoint("w4-cuarto-der")
    }

    /// «Trazos antes de las letras» (ADR 0031), the second day she goes to the island of letters, with the straight paths
    /// already done so that the curved ones come: Comeletras first, its cookies touched in turn, and then the waves, the
    /// loops and the snail, each traced with the finger in short strokes along it, as the game keeps where she left Bruno.
    func testPaths() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        stage(262, 258).tap()
        sleep(8)
        for _ in 1...8 {
            for x: CGFloat in [485, 600, 715] {
                stage(x, 252).tap()
                sleep(3)
            }
        }
        sleep(4)
        checkpoint("c1-caminos-izq")
        for (i, shape) in ["olas", "bucles", "caracol"].enumerated() {
            let points = pathPoints(shape)
            for (from, to) in zip(points, points.dropFirst()) {
                stage(from.x, from.y).press(forDuration: 0.05, thenDragTo: stage(to.x, to.y), withVelocity: 300, thenHoldForDuration: 0.05)
            }
            sleep(6)
            if i == 0 {
                checkpoint("c2-caminando-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("c3-regalo-der")
    }

    /// Points along one of Bruno's curved paths, as the game draws them (game/Paths.kt), close enough for short strokes.
    private func pathPoints(_ shape: String) -> [CGPoint] {
        (0...90).map { i in
            let u = CGFloat(i) / 90
            switch shape {
            case "olas":
                return CGPoint(x: 140 + 520 * u, y: 228 + 56 * sin(2 * .pi * 2.5 * u))
            case "bucles":
                let t = -CGFloat.pi + 2 * .pi * 3 * u
                return CGPoint(x: 150 + 26 * (t + .pi) - 70 * sin(t), y: 228 - 70 * cos(t))
            default:
                let angle = 2 * .pi * 2 * u
                let radius = 122 * (1 - 0.74 * u)
                return CGPoint(x: 400 + radius * cos(angle), y: 222 + radius * sin(angle))
            }
        }
    }

    /// «Sumas hasta 3» (ADR 0029), the third day she goes to the island of numbers: the strawberries first, three times
    /// round the bush, and then each sum, touching what is far from where the present will be, so that the route ends
    /// with it closed: two strawberries of the bush, which give her the ones she has to give or, once the numbers are
    /// out, are on the two and on the three; and then the two, from the top of its card, and the three.
    func testSums() {
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
        for _ in 1...3 {
            for (x, y) in berries {
                stage(x, y).tap()
                usleep(1_200_000)
            }
        }
        sleep(6)
        checkpoint("m1-sumas-izq")
        for turn in 1...5 {
            for (x, y, wait) in [(CGFloat(648), CGFloat(180), UInt32(3)), (706, 200, 5), (626, 190, 6), (726, 236, 6)] {
                stage(x, y).tap()
                sleep(wait)
            }
            if turn == 1 {
                checkpoint("m2-sumando-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("m3-regalo-der")
    }

    /// «Dentro y fuera» (ADR 0028): the island of shapes, its house built by touching the pieces of the tray in turn, and
    /// then the five places, without knowing which is asked: from where Bruno, the ball or the big box are, each dragged to
    /// every place in turn, waiting after each for the voice of a mistake; and the present.
    func testPlaces() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        stage(340, 78).tap()
        sleep(8)
        for _ in 1...8 {
            for x: CGFloat in [568, 658, 748] {
                stage(x, 344).tap()
                sleep(3)
            }
        }
        sleep(4)
        checkpoint("f1-sitios-izq")
        let from: [(CGFloat, CGFloat)] = [(86, 316), (172, 326), (301, 272)]
        let to: [(CGFloat, CGFloat)] = [(301, 240), (481, 280), (666, 180), (666, 320), (130, 330)]
        for turn in 1...7 {
            for (x, y) in from {
                for (toX, toY) in to {
                    stage(x, y).press(forDuration: 0.3, thenDragTo: stage(toX, toY), withVelocity: 600, thenHoldForDuration: 0.2)
                    sleep(3)
                }
            }
            if turn == 1 {
                checkpoint("f2-sitios-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("f3-regalo-der")
    }

    /// «El anterior y el siguiente» (ADR 0026), the second day she goes to the island of numbers: the strawberries first, as
    /// in the countdown, and then the number line, its squares touched in turn until each question is answered, and the
    /// present. The squares are touched low, under where the present will be, so that the touches left over do not open it.
    /// Touches while Bruno answers and jumps count for nothing, so a question can take two rounds of the line: twelve rounds
    /// are enough for the five.
    func testNumberLine() {
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
        sleep(4)
        checkpoint("r1-recta-izq")
        for turn in 1...12 {
            for square in 0...10 {
                stage(60 + 68 * CGFloat(square), 372).tap()
                usleep(1_200_000)
            }
            if turn == 2 {
                checkpoint("r2-saltando-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("r3-regalo-der")
    }

    /// «Ordena la historia» (ADR 0025): the island of feelings, its five rounds got through by touching each face in turn
    /// and then helping Bruno every way at once (a touch held, for the breathing, and the lamp, for the fear); then the two
    /// stories, touching the pictures in turn until each one is in order, and the present. The pictures are touched low,
    /// under where the present will be, so that the touches left over once the stories are done do not open it.
    func testStory() {
        XCUIDevice.shared.orientation = .landscapeLeft
        launchPastStart()
        sleep(2)
        stage(680, 290).tap()
        sleep(3)
        stage(112, 150).tap()
        sleep(8)
        for _ in 1...5 {
            for x: CGFloat in [512, 622, 732] {
                stage(x, 326).tap()
                sleep(3)
            }
            stage(300, 236).press(forDuration: 3.0)
            sleep(2)
            stage(120, 218).tap()
            sleep(4)
        }
        checkpoint("h1-historia-izq")
        for turn in 1...8 {
            for x: CGFloat in [347, 507, 667] {
                stage(x, 360).tap()
                sleep(3)
            }
            if turn == 2 {
                checkpoint("h2-ordenando-izq")
                XCUIDevice.shared.orientation = .landscapeRight
                sleep(2)
            }
        }
        checkpoint("h3-regalo-der")
    }
}

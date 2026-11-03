import SwiftUI

// An empty app: the UI test bundle needs a host, but it drives Bruno by its bundle identifier.
@main
struct HostApp: App {
    var body: some Scene {
        WindowGroup { Text("Host") }
    }
}

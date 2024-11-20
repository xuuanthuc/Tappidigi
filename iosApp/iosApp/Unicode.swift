import Foundation

@objc public class UnicodeHelper: NSObject {
    @objc public static func toUnicode(from codePoint: Int) -> String {
        guard let scalar = UnicodeScalar(codePoint) else {
            return ""
        }
        return String(scalar)
    }
}

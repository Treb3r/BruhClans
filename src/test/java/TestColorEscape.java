import org.bukkit.ChatColor;

public class TestColorEscape {
    public static void main(String[] args) {
        String input = "&#00FF00+ &fЦвета названия: &#AAAAAA&&7 &#555555&&8";
        // simulate hex replacement
        String hex7 = "\u00A7x\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a\u00A7a";
        String hex8 = "\u00A7x\u00A75\u00A75\u00A75\u00A75\u00A75\u00A75";
        String replaced = input.replace("&#AAAAAA", hex7).replace("&#555555", hex8).replace("&#00FF00", "\u00A7a");

        // with placeholder
        String temp = replaced.replace("&&", "\uFFF0");
        String translated = ChatColor.translateAlternateColorCodes('&', temp);
        String result = translated.replace("\uFFF0", "&");

        System.out.println("Result: " + result);
        for (char c : result.toCharArray()) {
            if (c == '\u00A7') System.out.print("[SECTION]");
            else System.out.print(c);
        }
        System.out.println();
    }
}

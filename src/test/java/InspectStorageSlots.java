import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class InspectStorageSlots {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-01-49.jpg"));
        // Find centers of connected components of gold pixels
        boolean[][] visited = new boolean[img.getWidth()][img.getHeight()];
        List<int[]> centers = new ArrayList<>();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (visited[x][y]) continue;
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 190 && g > 130 && g < 185 && b < 60) {
                    // BFS component
                    long sx = 0, sy = 0;
                    int count = 0;
                    Queue<int[]> q = new LinkedList<>();
                    q.add(new int[]{x, y});
                    visited[x][y] = true;
                    while (!q.isEmpty()) {
                        int[] cur = q.poll();
                        sx += cur[0]; sy += cur[1];
                        count++;
                        int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                        for (int[] d : dirs) {
                            int nx = cur[0] + d[0], ny = cur[1] + d[1];
                            if (nx >= 0 && nx < img.getWidth() && ny >= 0 && ny < img.getHeight() && !visited[nx][ny]) {
                                int nrgb = img.getRGB(nx, ny);
                                int nr = (nrgb >> 16) & 0xff;
                                int ng = (nrgb >> 8) & 0xff;
                                int nb = nrgb & 0xff;
                                if (nr > 180 && ng > 120 && ng < 195 && nb < 70) {
                                    visited[nx][ny] = true;
                                    q.add(new int[]{nx, ny});
                                }
                            }
                        }
                    }
                    if (count > 30) {
                        centers.add(new int[]{(int)(sx / count), (int)(sy / count), count});
                    }
                }
            }
        }
        System.out.println("Found " + centers.size() + " gold objects:");
        for (int[] c : centers) {
            System.out.printf("  Center: (%d, %d), count=%d%n", c[0], c[1], c[2]);
        }
    }
}

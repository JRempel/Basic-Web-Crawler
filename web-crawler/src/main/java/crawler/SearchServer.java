package crawler;

public class SearchServer {
    public static void main(String[] args) {
        System.out.print("Content-type: text/html\n\n");
        System.out.print("<title>CGI Test from Java</title>\n");
//        System.out.print("<p>Hello World!</p>\n");
//        System.out.print("Received query: " + Arrays.toString(args) + " \n\n\n\n");

        Crawler crawler = new Crawler("http://www.hkbu.edu.hk/eng/main/index.jsp", 10, 10);
        crawler.start();
    }
}

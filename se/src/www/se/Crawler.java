package se;

import java.util.LinkedList;

/**
 * Created by YI Peipei on 8/19/2016.
 */
public class Crawler {
    LinkedList<String> urlPool = new LinkedList<>();
    int Y = -1;
    int X = -1;

    public Crawler(String url, int Y, int X) {
        urlPool.add(url);
        this.Y = Y;
        this.X = X;
    }

    public void start() {

    }

    public static void main(String[] args) {
        if (0 == args.length) {
            System.exit(-1);
        }

        String start_url = null;
        int Y = -1;
        int X = -1;
        if (1 <= args.length) {
            start_url = args[0];
        }
        if (2 <= args.length) {
            Y = Integer.parseInt(args[1]);
        }
        if (3 <= args.length) {
            X = Integer.parseInt(args[2]);
        }
        System.out.println("start crawling from " + start_url + " with Y=" + Y + " and X=" + X);
        Crawler crawler = new Crawler(start_url, Y, X);
        crawler.start();
    }
}
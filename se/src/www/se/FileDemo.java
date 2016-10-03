package se;

import java.io.*;

/**
 * Demonstrate basic file operations.
 * <p/>
 * Created by YI Peipei on 8/19/2016.
 */
public class FileDemo {
    /**
     * Write lines to a file (create it if not exists).
     *
     * @param fileName
     * @param text
     */
    public void write(String fileName, String text) {
        File file = new File(fileName);
        if (file.exists()) {
            System.out.println(fileName + " already exists, abort.");
            return;
        }

        try {
            file.createNewFile();
            FileWriter fw = new FileWriter(file);
            fw.write(text);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("File created at " + file.getAbsolutePath());
    }

    /**
     * Read line by line from a file and print to console.
     *
     * @param fileName
     */
    public void read(String fileName) {
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        String fileName = "example.txt";
        String text = "Example File\n" +
                "\n" +
                "This file is created to illustrate basic file operations.\n";

        FileDemo fileDemo = new FileDemo();
        System.out.println("### Write lines to a file.");
        fileDemo.write(fileName, text);
        System.out.println("### Read lines from a file.");
        fileDemo.read(fileName);
    }
}

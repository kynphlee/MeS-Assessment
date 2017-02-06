package com.mes;

import com.mes.parser.HTMLIteratorParser;
import com.mes.parser.Parser;

import javax.swing.text.BadLocationException;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by kynphlee on 2/1/17.
 */
public class HTMLParser {
    public static void main(String[] args) {
        Path urlPath = Paths.get(args[0]);
        Charset charset = Charset.forName("US-ASCII");
        String urlStr = null;
        Parser parser = new HTMLIteratorParser();

        try {
            BufferedReader bufferedReader = Files.newBufferedReader(urlPath, charset);
            urlStr = bufferedReader.readLine();
            bufferedReader.close();

            System.out.printf("Parsing url: %s...%n", urlStr);
            parser.parse(urlStr);
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        System.out.printf("Total requests: %d%n", parser.getRequestCount());
        System.out.printf("Total size: %.2f kb%n", (parser.getTotalSize() * 0.001));
    }
}

package com.mes.parser;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by kynphlee on 2/3/17.
 */
public interface Parser {
    void parse(String urlStr) throws IOException, BadLocationException;

    long getTotalSize();

    int getRequestCount();
}

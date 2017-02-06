package com.mes.parser;

import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HTMLIteratorParser implements Parser {
    private Map<HTML.Tag, Long> tagCount = null;
    private Map<String, Integer> counter = null;

    public HTMLIteratorParser() {
        tagCount = new HashMap<>();
        counter = new HashMap<>();
        counter.put("requestCount", 0);
    }

    @Override
    public void parse(String urlStr) throws IOException, BadLocationException {
        URL url = new URL(urlStr);
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument();
        Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
        kit.read(HTMLReader, doc, 0);

        ElementIterator it = new ElementIterator(doc);
        Element elem;

        String s = null;
        Long contentLength;
        while ((elem = it.next()) != null) {
            switch (elem.getName()) {
                case "img":
                    contentLength = getContentLength(elem, HTML.Attribute.SRC, url);
                    if (contentLength != -1) {
                        addToTagCount(tagCount, counter, HTML.Tag.IMG, contentLength);
                    }
                    break;
                case "script":
                    contentLength = getContentLength(elem, HTML.Attribute.SRC, url);
                    if (contentLength != -1) {
                        addToTagCount(tagCount, counter, HTML.Tag.SCRIPT, contentLength);
                    }
                    break;
                case "link":
                    contentLength = getContentLength(elem, HTML.Attribute.HREF, url);
                    if (contentLength != -1) {
                        addToTagCount(tagCount, counter, HTML.Tag.LINK, contentLength);
                    }
                    break;
                case "comment":
                    contentLength = getContentLength(elem, HTML.Attribute.COMMENT, url);
                    if (contentLength != -1) {
                        addToTagCount(tagCount, counter, HTML.Tag.COMMENT, contentLength);
                    }
                    break;
            }
        }
    }

    private void addToTagCount(Map<HTML.Tag, Long> tagMap,
                               Map<String, Integer> counter,
                               HTML.Tag tag, long length) {
        if (tagMap.get(tag) == null) {
            tagMap.put(tag, length);
        } else {
            long size = tagMap.get(tag);
            tagMap.put(tag, length + size);
        }
        int count = counter.get("requestCount");
        counter.put("requestCount", ++count);
    }

    private long getContentLength(Element elem,HTML.Attribute attr, URL contextUrl)  {
        URLConnection conn = null;
        AttributeSet linkAttrSet = elem.getAttributes();
        if (linkAttrSet.isDefined(attr)) {
            String s = (String) elem.getAttributes().getAttribute(attr);
            URL linkUrl = null;
            try {
                linkUrl = new URL(contextUrl, s);
                conn = linkUrl.openConnection();
                conn.connect();
            } catch (MalformedURLException e) {
                switch (elem.getName()) {
                    case "link":
                        System.err.printf("Skipping malformed url: %s%n", s);
                        break;
                    default:
                        e.printStackTrace();
                }
                return -1;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return conn.getContentLengthLong();
        }
        return -1;
    }

    @Override
    public long getTotalSize() {
        long totalSize = 0;
        for(Map.Entry<HTML.Tag, Long> entry: tagCount.entrySet()) {
            totalSize += entry.getValue();
        }
        return totalSize;
    }

    @Override
    public int getRequestCount() {
        return counter.get("requestCount");
    }
}

package com.kazurayam.markdownutils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PandocMarkdownTranslator {

    /**
     * current working directory
     */
    private final Path cwd;

    public PandocMarkdownTranslator() {
        cwd = Paths.get(System.getProperty("user.dir"));
    }

    public static void main(String[] args) throws IOException {
        PandocMarkdownTranslator translator = new PandocMarkdownTranslator();
        if (args.length == 1) {
            translator.translateFile(args[0]);
        } else if (args.length == 2) {
            translator.translateFile(args[0], args[1]);
        } else {
            System.out.println("Usage: java -jar MarkdownUtils-x.x.x.jar README.md");
            System.out.println("    or java -jar MarkdownUtils-x.x.x.jar README.md temp.md");
            System.exit(1);
        }
    }

    public void translateFile(String inputFilePath, String outputFilePath) throws IOException {
        Objects.requireNonNull(inputFilePath);
        Objects.requireNonNull(outputFilePath);
        Path input = cwd.resolve(inputFilePath);
        if (!Files.exists(input)) {
            throw new FileNotFoundException(input + " is not found");
        }
        Path output = cwd.resolve(outputFilePath);
        Path outputParent = output.getParent();
        if (!Files.exists(outputParent)) {
            Files.createDirectories(outputParent);
        }
        this.translateFile(input, output);
    }

    public void translateFile(String filePath) throws IOException {
        Objects.requireNonNull(filePath);
        Path input = cwd.resolve(filePath);
        if (!Files.exists(input)) {
            throw new FileNotFoundException(input + " is not found");
        }
        Path tempFile = Files.createTempFile(PandocMarkdownTranslator.class.getSimpleName(), "");
        // translate the input into the temporary file
        this.translateFile(input, tempFile);
        // copy the translated text over the original
        Files.copy(tempFile, input, StandardCopyOption.REPLACE_EXISTING);
    }

    public void translateFile(Path input, Path output) throws IOException {
        Reader reader = new InputStreamReader(
                new FileInputStream(input.toFile()), StandardCharsets.UTF_8);
        Writer writer = new OutputStreamWriter(
                new FileOutputStream(output.toFile()), StandardCharsets.UTF_8);
        this.translateContent(reader, writer);
        reader.close();
        writer.flush();
        writer.close();
    }

    public void translateContent(Reader reader, Writer writer) throws IOException {
        Objects.requireNonNull(reader);
        Objects.requireNonNull(writer);
        BufferedReader br = new BufferedReader(reader);
        PrintWriter pw = new PrintWriter(new BufferedWriter(writer));
        boolean inPrologue = true;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                inPrologue = false;
            }
            if (inPrologue) {
                String translated = this.translateTocLink(line);
                pw.println(translated);
            } else {
                pw.println(line);
            }
        }
        pw.flush();
    }

    /**
     * Translate a line of "Table of content" generated by Pandoc into
     * a string compliant to GitHub Flavoured Markdown. For example,
     * I have a line:
     *
     * <PRE>
     * -    [My previous solution](#_my_previous_solution)
     * </PRE>
     * <p>
     * I want to translate this into
     *
     * <PRE>
     * -    [My previous solution](#my-previous-solution)
     * </PRE>
     *
     * @param line a line from README.md file
     */
    String translateTocLink(String line) {
        Matcher m = pattern.matcher(line);
        if (m.matches()) {
            return m.group(1) + "(#" + m.group(2).replace("_", "-") + ")";
        } else {
            return line;
        }
    }

    static final String PTN_LEAD = "\\s*-\\s+";
    static final String PTN_TITLE = "[^\\]]*";
    static final String PTN_HEADER = "(" + PTN_LEAD + "\\[" + PTN_TITLE + "\\]" + ")";
    static final String PTN_LINK_PREFIX = "#_";
    static final String PTN_ID = "[^\\)]*";
    static final String PTN_LINK = "\\(" + PTN_LINK_PREFIX + "(" + PTN_ID + ")" + "\\)";
    /**
     * a Regular Expression that matches a string like
     * <PRE>
     * -    [foo](#_bar_baz)
     * </PRE>
     */
    public static final String PTN_TOC_LINE = "^" + PTN_HEADER + PTN_LINK + "$";
    private static final Pattern pattern = Pattern.compile(PTN_TOC_LINE);

}
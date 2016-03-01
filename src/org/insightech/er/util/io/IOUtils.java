package org.insightech.er.util.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class IOUtils {

    public static void closeQuietly(final InputStream input) {
        try {
            if (input != null)
                input.close();
        } catch (final IOException ioe) {}
    }

    public static void closeQuietly(final OutputStream output) {
        try {
            if (output != null)
                output.close();
        } catch (final IOException ioe) {}
    }

    public static void closeQuietly(final Writer writer) {
        try {
            if (writer != null)
                writer.close();
        } catch (final IOException ioe) {}
    }

    public static int copy(final InputStream input, final OutputStream output) throws IOException {
        final byte buffer[] = new byte[4096];
        int count = 0;
        for (int n = 0; -1 != (n = input.read(buffer));) {
            output.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

    public static void copy(final InputStream input, final Writer output) throws IOException {
        final InputStreamReader in = new InputStreamReader(input);
        copy(((in)), output);
    }

    public static int copy(final Reader input, final Writer output) throws IOException {
        final char buffer[] = new char[4096];
        int count = 0;
        for (int n = 0; -1 != (n = input.read(buffer));) {
            output.write(buffer, 0, n);
            count += n;
        }

        return count;
    }

    public static byte[] toByteArray(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        copy(input, output);
        return output.toByteArray();
    }

    public static String toString(final InputStream input) throws IOException {
        final StringWriter sw = new StringWriter();
        copy(input, sw);
        return sw.toString();
    }

    public static void write(final String data, final OutputStream output) throws IOException {
        if (data != null)
            output.write(data.getBytes());
    }

    public static void write(final String data, final OutputStream output, final String encoding) throws IOException {
        if (data != null)
            if (encoding == null)
                write(data, output);
            else
                output.write(data.getBytes(encoding));
    }
}

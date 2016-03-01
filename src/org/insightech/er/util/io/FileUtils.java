package org.insightech.er.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.insightech.er.util.Check;

public class FileUtils {

    public static List<File> getChildren(final File file) {
        final List<File> children = new ArrayList<File>();

        if (file.isDirectory()) {
            for (final File child : file.listFiles()) {
                children.addAll(getChildren(child));
            }

        } else {
            children.add(file);
        }

        return children;
    }

    public static void deleteDirectory(final File directory) throws IOException {
        if (!directory.exists())
            return;
        cleanDirectory(directory);
        if (!directory.delete()) {
            final String message = "Unable to delete directory " + directory + ".";
            throw new IOException(message);
        } else {
            return;
        }
    }

    public static void cleanDirectory(final File directory) throws IOException {
        if (!directory.exists()) {
            final String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }
        if (!directory.isDirectory()) {
            final String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }
        final File files[] = directory.listFiles();
        if (files == null)
            throw new IOException("Failed to list contents of " + directory);
        IOException exception = null;
        for (int i = 0; i < files.length; i++) {
            final File file = files[i];
            try {
                forceDelete(file);
            } catch (final IOException ioe) {
                exception = ioe;
            }
        }

        if (null != exception)
            throw exception;
        else
            return;
    }

    public static void forceDelete(final File file) throws IOException {
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            if (!file.exists())
                throw new FileNotFoundException("File does not exist: " + file);
            if (!file.delete()) {
                final String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }
    }

    public static void copyFile(final File srcFile, final File destFile) throws IOException {
        copyFile(srcFile, destFile, true);
    }

    public static void copyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        if (srcFile == null)
            throw new NullPointerException("Source must not be null");
        if (destFile == null)
            throw new NullPointerException("Destination must not be null");
        if (!srcFile.exists())
            throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
        if (srcFile.isDirectory())
            throw new IOException("Source '" + srcFile + "' exists but is a directory");
        if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath()))
            throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
        if (destFile.getParentFile() != null && !destFile.getParentFile().exists() && !destFile.getParentFile().mkdirs())
            throw new IOException("Destination '" + destFile + "' directory cannot be created");
        if (destFile.exists() && !destFile.canWrite()) {
            throw new IOException("Destination '" + destFile + "' exists but is read-only");
        } else {
            doCopyFile(srcFile, destFile, preserveFileDate);
            return;
        }
    }

    private static void doCopyFile(final File srcFile, final File destFile, final boolean preserveFileDate) throws IOException {
        if (destFile.exists() && destFile.isDirectory())
            throw new IOException("Destination '" + destFile + "' exists but is a directory");
        final FileInputStream input = new FileInputStream(srcFile);
        try {
            final FileOutputStream output = new FileOutputStream(destFile);
            try {
                IOUtils.copy(input, output);
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(input);
        }
        if (srcFile.length() != destFile.length())
            throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
        if (preserveFileDate)
            destFile.setLastModified(srcFile.lastModified());
    }

    public static byte[] readFileToByteArray(final File file) throws IOException {
        java.io.InputStream in = null;
        try {
            in = new FileInputStream(file);
            final byte abyte0[] = IOUtils.toByteArray(in);
            return abyte0;
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static void writeByteArrayToFile(final File file, final byte data[]) throws IOException {
        final OutputStream out = new FileOutputStream(file);
        try {
            out.write(data);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static void writeStringToFile(final File file, final String data, final String encoding) throws IOException {
        final OutputStream out = new FileOutputStream(file);
        try {
            IOUtils.write(data, out, encoding);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    public static File getFile(final File baseDir, final String filePath) {
        File file = new File(filePath);

        if (!file.isAbsolute()) {
            file = new File(baseDir, filePath);
        }

        return file;
    }

    public static boolean isInBaseDir(final File baseDir, final File file) {
        if (file.getAbsolutePath().equals(baseDir.getAbsolutePath())) {
            return true;

        } else if (file.getAbsolutePath().startsWith(baseDir.getAbsolutePath() + File.separator)) {
            return true;
        }

        return false;
    }

    public static boolean isInBaseDir(final File baseDir, final String filePath) throws IOException {
        final File file = getFile(baseDir, filePath);

        return isInBaseDir(baseDir, file);
    }

    public static String getRelativeFilePath(final File baseDir, final String absoluteFilePath) {
        if (Check.isEmpty(absoluteFilePath)) {
            return "";
        }

        final File file = new File(absoluteFilePath);

        if (isInBaseDir(baseDir, file)) {
            if (file.getAbsolutePath().length() > baseDir.getAbsolutePath().length()) {
                return file.getAbsolutePath().substring(baseDir.getAbsolutePath().length() + 1);
            } else {
                return "";
            }
        }

        return absoluteFilePath;
    }

    public static boolean isAbsolutePath(final String path) {
        if (Check.isEmpty(path)) {
            return false;
        }
        return new File(path).isAbsolute();
    }

}

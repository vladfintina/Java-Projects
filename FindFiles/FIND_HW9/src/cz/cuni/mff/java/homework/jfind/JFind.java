package cz.cuni.mff.java.homework.jfind;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.FileVisitResult.CONTINUE;

public class JFind {

    public static class Finder
            extends SimpleFileVisitor<Path> {

        private Pattern matchS = null;
        private Pattern matchR = null;
        private Pattern matchI = null;
        private Long Size = null;
        private Long SSize = null;
        private Long BSize = null;
        private final String[] args;
        private String startDir;


        private final String BSIZE = "-bsize";
        private final String SSIZE = "-ssize";
        private final String SIZE = "-size";
        private final String REGEX = "-regex";
        private final String InsensitiveNAME = "-iname";
        private final String NAME = "-name";

        private final String[] constr = new String[] {NAME, InsensitiveNAME, REGEX, SIZE, SSIZE, BSIZE};

        private String convertGlobalToRegex(String line)
        {
            line = line.trim();
            int strLen = line.length();
            StringBuilder sb = new StringBuilder(strLen);
            boolean escaping = false;
            int inCurlies = 0;
            for (char currentChar : line.toCharArray())
            {
                switch (currentChar)
                {
                    case '*':
                        if (escaping)
                            sb.append("\\*");
                        else
                            sb.append(".*");
                        escaping = false;
                        break;
                    case '?':
                        if (escaping)
                            sb.append("\\?");
                        else
                            sb.append('.');
                        escaping = false;
                        break;
                    case '.':
                    case '(':
                    case ')':
                    case '+':
                    case '|':
                    case '^':
                    case '$':
                    case '@':
                    case '%':
                        sb.append('\\');
                        sb.append(currentChar);
                        escaping = false;
                        break;
                    case '\\':
                        if (escaping)
                        {
                            sb.append("\\\\");
                            escaping = false;
                        }
                        else
                            escaping = true;
                        break;
                    case '{':
                        if (escaping)
                        {
                            sb.append("\\{");
                        }
                        else
                        {
                            sb.append('(');
                            inCurlies++;
                        }
                        escaping = false;
                        break;
                    case '}':
                        if (inCurlies > 0 && !escaping)
                        {
                            sb.append(')');
                            inCurlies--;
                        }
                        else if (escaping)
                            sb.append("\\}");
                        else
                            sb.append("}");
                        escaping = false;
                        break;
                    case ',':
                        if (inCurlies > 0 && !escaping)
                        {
                            sb.append('|');
                        }
                        else if (escaping)
                            sb.append("\\,");
                        else
                            sb.append(",");
                        break;
                    default:
                        escaping = false;
                        sb.append(currentChar);
                }
            }
            return sb.toString();
        }

        //validate the path directory
        private boolean validateDirectory() {
            startDir = args[0];
            Path pathDir = Paths.get(startDir);
            return Files.isDirectory(pathDir);
        }

        //validate the size constraints
        private long calculateSize(String val) {
            long s;
            if(val.charAt(val.length()-1) == 'k') {
                val = val.substring(0, val.length()-1);
                s = Long.parseLong(val);
                s = 1024*s;

            }
            else if(val.charAt(val.length()-1) == 'M') {
                val = val.substring(0, val.length()-1);
                s = Long.parseLong(val);
                s = 1024*1024*s;
            }
            else {
                s = Long.parseLong(val);
            }
            return s;
        }

        //process all the constraints and their value
        private boolean processConstr(String constr, String val) {
            switch (constr) {
                case NAME -> {
                    matchS = Pattern.compile(convertGlobalToRegex(val));
                    return true;
                }
                case InsensitiveNAME ->  {
                    matchI = Pattern.compile("(?i)" + convertGlobalToRegex(val));
                    return true;
                }
                case REGEX -> {
                    matchR = Pattern.compile(val);
                    return true;
                }
                case SIZE -> {
                    try {
                        Size = calculateSize(val);
                    }
                    catch (NumberFormatException ex) {
                        return false;
                    }
                    return true;
                }
                case SSIZE -> {
                    //throw  new RuntimeException();
                    try {
                        SSize = calculateSize(val);
                    }
                    catch (NumberFormatException ex) {
                        return false;
                    }
                    return true;
                }
                case BSIZE -> {
                    //throw new RuntimeException();
                    try {
                        BSize = calculateSize(val);
                    }
                    catch (NumberFormatException ex) {
                        return false;
                    }
                    return true;
                }
                default ->  {
                    return false;
                }
            }
        }

        //validate all the arguments (the flags)
        private boolean validateArgs() {
            if (args.length < 1) {
                return false;
            }
            startDir = args[0];
            if (args.length % 2 != 1)
                return false;

            for (int i = 1; i < args.length; i=i+2) {
                if(!processConstr(args[i], args[i+1])) {
                    return false;
                }
            }
            return true;
        }

        Finder(String[] args) {
            this.args = args;
        }

        // Compares the pattern against the file
        void find(Path file) {
            Path name = file.getFileName();
            String pathS = file.toString();
            long size;
            try {
                var fileChannel = FileChannel.open(file);
                size = fileChannel.size();
            }
            catch (IOException ex) {
                size = 0;
            }
            if (name != null) {
                boolean matches = true;
                if(matchI != null) {
                    Matcher m = matchI.matcher(name.toString());
                    matches = m.matches();
                }
                if(matchR != null) {
                    Matcher m = matchR.matcher(name.toString());
                    matches = m.matches();
                }
                if(matchS != null) {
                    Matcher m = matchS.matcher(name.toString());
                    matches = m.matches();
                }
                if(Size != null && size != Size) {
                    matches = false;
                }
                if(SSize != null && size >= SSize) {
                    matches = false;
                }
                if(BSize != null && size <= BSize) {
                    matches = false;
                }
                if(matches) {
                    String subpath = pathS.substring(startDir.length()+1);
                    System.out.println(subpath);
                }
            }
        }

        // Invoke the pattern matching
        // method on each file.
        @Override
        public FileVisitResult preVisitDirectory(Path dir,
                                                 BasicFileAttributes attrs) {
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file,
                                         BasicFileAttributes attrs) throws IOException {
            find(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file,
                                               IOException exc) {
            return CONTINUE;
        }
    }

    public static void find(String[] args)  {
        try {
            Finder finder = new Finder(args);
            if(args.length >= 1 && finder.validateDirectory()) {
                if (finder.validateArgs()) {
                    Path startingDir = Paths.get(args[0]);
                    Files.walkFileTree(startingDir, finder);
                }
                else
                    System.out.println("ERROR");
            }
        }
        catch (IOException ex) {

        }

    }
}

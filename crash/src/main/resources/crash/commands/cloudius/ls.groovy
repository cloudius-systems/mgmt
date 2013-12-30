package crash.commands.cloudius

import com.cloudius.cli.command.OSvCommand
import com.cloudius.cli.completers.PathCompleter
import org.crsh.cli.*
import org.crsh.cli.descriptor.ParameterDescriptor
import org.crsh.cli.spi.Completer
import org.crsh.cli.spi.Completion
import org.crsh.text.ui.UIBuilder

import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.PosixFilePermission

@Usage("list directory contents")
class ls extends OSvCommand implements Completer {

  private Boolean flagRecursive = false
  private Boolean flagLongFormat = false
  private Boolean flagSortBySize = false
  private Boolean flagSortByTime = false
  private Boolean flagSortByExtension = false
  private Boolean flagSortReverse = false

  // Calendar boundaries to decide whether to display the time or year in the mtime columns
  private Date displayYearBefore = null;
  private Date displayYearAfter = null;

  @Man("List information about the files (the current directory by default)")
  @Command
  void main(
      @Option(names=['R', 'recursive']) @Usage('list subdirectories recursively') Boolean recursive,
      @Option(names=['l']) @Usage("use a long listing format") Boolean longFormat,
      @Option(names=['S']) @Usage("sort by file size") Boolean sortBySize,
      @Option(names=['t']) @Usage("sort by modification time, newest first") Boolean sortByTime,
      @Option(names=['X']) @Usage("sort alphabetically by entry extension") Boolean sortByExtension,
      @Option(names=['r']) @Usage("reverse order while sorting") Boolean sortReverse,
      @Argument(completer = ls.class, name = '[FILE]') @Usage("The files to display") List<String> files
  ) {
    // Some logistics
    this.flagRecursive = recursive.asBoolean();
    this.flagLongFormat = longFormat.asBoolean();
    this.flagSortBySize = sortBySize.asBoolean();
    this.flagSortByTime = sortByTime.asBoolean();
    this.flagSortByExtension = sortByExtension.asBoolean();
    this.flagSortReverse = sortReverse.asBoolean();

    def cal = Calendar.getInstance();
    cal.add(Calendar.MONTH, -6);
    this.displayYearBefore = cal.getTime();
    cal.add(Calendar.YEAR, 1);
    this.displayYearAfter = cal.getTime();

    // Convert all requested paths to our internal map structure, while maintaining the original
    // command line arguments: [file: File, lbl: String] for printing
    def pathsToDisplay = (files ? files : ['.']).collect({[file: getResolvedPath(it), lbl: it]})
                         .findAll({verifyExists(it)})

    if (pathsToDisplay.size() > 0) {
      if (pathsToDisplay.size() == 1 && !flagRecursive) {
        out.print(preparePrint(pathsToDisplay.first()))
      } else {
        // Separate files from directories: 0: directories, 1: files
        def pathsSplitted = pathsToDisplay.split({it.file.isDirectory()})

        // First, print all of the files
        if (pathsSplitted[1].size > 0) {
          out.println(preparePrint(pathsSplitted[1]))
        }

        // Print the directories, recursivley if needed
        def printDirectories = null
        printDirectories = { directories ->
          directories.each { dir ->
            out.println(dir.lbl + ':')
            out.println(preparePrint(dir))

            if (flagRecursive) {
              out.flush()
              def more = dir.file.listFiles({f -> f.isDirectory()} as FileFilter)
              if (more) {
                printDirectories(sortByFlags(more.collect({
                  [file: it, lbl: dir.lbl + (dir.lbl.endsWith('/') ? '' : '/') + it.getName()]
                })))
              }
            }
          }
        }

        printDirectories(pathsSplitted[0])
      }
    }
  }

  @Override
  Completion complete(ParameterDescriptor parameter, String prefix) throws Exception {
    new PathCompleter(context.getSession().get("getCurrentPath")()).complete(
        parameter, prefix
    )
  }

  /**
   * Returns the path ready for printing, with formatting flags
   * If it's a file, it returns the file
   * If it's a directory, it lists the contents of the directory
   * If a list is supplied, it is treated as a list of files/directories to be printed as is
   *
   * @param path The path to prepare/format. If it's a file, it returns the path ready to print in requested format. If
   *             it's a directory, it lists the contents of the directory. If a list is supplied, it is formatted as is
   * @return
   */
  private Object preparePrint(Object path) {
    def these = []

    if (path instanceof List<?>) {
      these = path
    } else {
      def resolvedPath = path.file
      if (resolvedPath.isDirectory()) {
        def listFiles = resolvedPath.listFiles()
        if (!listFiles) {
          error("cannot open directory ${path.lbl}: Persmission denied")
          return ''
        }

        these = ['.', '..'].collect { [file: new File(resolvedPath as File, it), lbl: it] }
        these += resolvedPath.listFiles().findAll({
          verifyExists([file: it, lbl: path.lbl + '/' + it.getName()])
        }).collect({[file: it, lbl: it.getName()]})

        these = sortByFlags(these)
      } else {
        these = [path]
      }
    }

    if (!flagLongFormat) {
      itemsToColumns(these.collect({ it.lbl }) as ArrayList<String>, context.width as Integer) + '\n'
    } else {
      // Some aggregated data for proper formatting
      def aggregated = [nlinkColPad: 0, sizeColPad: 0]

      // Collect more data on our directory
      def withAttributes = these.collect {
        def nioPath = Paths.get(it.file.getAbsolutePath())
        def attr = Files.readAttributes(nioPath, 'unix:*')

        aggregated.nlinkColPad = Math.max(aggregated.nlinkColPad, attr.get('nlink') as Integer)
        aggregated.sizeColPad  = Math.max(aggregated.sizeColPad,  attr.get('size') as Integer)

        return [
            file: it.file,
            lbl: it.lbl,
            nioPath: nioPath,
            attr: attr,
            date: new Date(it.file.lastModified() as long)
        ]
      }

      aggregated.nlinkColPad = aggregated.nlinkColPad.toString().size()
      aggregated.sizeColPad  = aggregated.sizeColPad.toString().size()

      def uiBuilder = new UIBuilder()
      uiBuilder.table(rightCellPadding:1) {
        withAttributes.each { p ->
          row {
            label(modForPath(p))
            label(String.format("%" + aggregated.nlinkColPad + "s", p.attr.get('nlink')))
            label(p.attr.get('owner'))
            label(p.attr.get('group'))
            label(String.format("%" + aggregated.sizeColPad + "s", p.attr.get('size')))
            label(p.date.format('MMM'))
            label(String.format("%2s", p.date.format('d')))

            if (p.date.before(this.displayYearBefore) || p.date.after(this.displayYearAfter)) {
              label(String.format("%5s", p.date.format('yyyy')))
            } else {
              label(p.date.format('HH:mm'))
            }

            label(p.lbl)
          }
        }
      }
    }
  }

  private ArrayList<?> sortByFlags(ArrayList<?> paths) {
    def sorted = paths.sort {
      def file = it.file
      if (this.flagSortByExtension) {
        return getExtension(file.getName()).toLowerCase()
      } else if (this.flagSortBySize) {
        return Files.getAttribute(Paths.get(file.getAbsolutePath()), 'unix:size') * -1 // larger first
      } else if (this.flagSortByTime) {
        return file.lastModified() * -1 // newest first
      } else {
        return (file.getName().startsWith('.') ? file.getName().substring(1) : file.getName()).toLowerCase()
      }
    }

    return this.flagSortReverse ? sorted.reverse(true) : sorted
  }

  private Boolean verifyExists(Object path) {
    if (!path.file.exists()) {
      error("could not access ${path.lbl}: No such file or directory")
      return false
    }
    return true
  }

  private void error(String msg) {
    // TODO: We don't have an err output
    out.println("ls: " + msg)
  }

  private static String modForPath(Object p) {
    def posix = Files.getPosixFilePermissions(p.nioPath)
    String.format('%s' * 10,
        p.file.isDirectory() ? 'd' : '-',
        posix.contains(PosixFilePermission.OWNER_READ) ? 'r' : '-',
        posix.contains(PosixFilePermission.OWNER_WRITE) ? 'w' : '-',
        posix.contains(PosixFilePermission.OWNER_EXECUTE) ? 'x' : '-',
        posix.contains(PosixFilePermission.GROUP_READ) ? 'r' : '-',
        posix.contains(PosixFilePermission.GROUP_WRITE) ? 'w' : '-',
        posix.contains(PosixFilePermission.GROUP_EXECUTE) ? 'x' : '-',
        posix.contains(PosixFilePermission.OTHERS_READ) ? 'r' : '-',
        posix.contains(PosixFilePermission.OTHERS_WRITE) ? 'w' : '-',
        posix.contains(PosixFilePermission.OTHERS_EXECUTE) ? 'x' : '-'
    )
  }

  private static String getExtension(String fileName) {
    def dotLastIndex = fileName.lastIndexOf('.')

    if (dotLastIndex == -1 || dotLastIndex == 0) {
      return ""
    } else {
      return fileName.substring(fileName.lastIndexOf('.'))
    }
  }
}

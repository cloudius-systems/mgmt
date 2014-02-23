package com.cloudius.cli.util;

public class SizeFormatter {
  public static enum Size {
    B (0),
    KB (1),
    MB (2),
    GB (3),
    TB (4),
    HUMAN (0);

    private final double exp;
    Size(double exp) {
      this.exp = exp;
    }
  }

  /**
   * Returns humanReadableSizeFormatter(bytes, "%.1f")
   * @param bytes  The size to format
   * @return Human readable formatted string (e.g. 16.1GB)
   */
  public static String humanReadableSizeFormatter(long bytes) {
    return humanReadableSizeFormatter(bytes, "%.1f");
  }

  /**
   * Returns bytes as a human readable string, formatted with `format`.
   * @param bytes  The size to format
   * @param format Used to format the numberic value
   * @return Human readable formatted string (e.g. 16.1GB)
   */
  public static String humanReadableSizeFormatter(long bytes, String format) {
    int unit = 1024;
    if (bytes < unit) return bytes + "B";
    int exp = (int) (Math.log(bytes) / Math.log(unit));
    char pre = "kMGTPE".charAt(exp-1);
    return String.format(format + "%c", bytes / Math.pow(unit, exp), pre);
  }

  /**
   * Formats the requested size, with the format %.1f
   * @param res    The size to format
   * @param units  The size unit
   * @return The formatted string, as requested
   */
  public static String sizeFormatter(long res, Size units) {
    return sizeFormatter(res, units, "%.1f");
  }

  /**
   * Formats the requested size, according to Size specified
   * @param res    The size to format
   * @param units  The size unit
   * @param format The format to use
   * @return The formatted string, as requested
   */
  public static String sizeFormatter(long res, Size units, String format) {
    switch (units) {
      case B:
        return String.format("%d", res);
      case HUMAN:
        return humanReadableSizeFormatter(res, format);
      default:
        return String.format(format, res / Math.pow(1024, units.exp));
    }
  }

  /**
   * Translates boolean size flag parameters to the proper size unit.
   * This is basically a short-hand method to not repeat this process with command-line arguments
   *
   * @return Return the proper size enum. Smallest unit takes precedence.
   */
  public static Size toSizeUnit(Boolean b, Boolean kb, Boolean mb, Boolean gb, Boolean tb, Boolean hr) {
    if (b != null && b) {
      return Size.B;
    } else if (kb != null && kb) {
      return Size.KB;
    } else if (mb != null && mb) {
      return Size.MB;
    } else if (gb != null && gb) {
      return Size.GB;
    } else if (tb != null && tb) {
      return Size.TB;
    } else if (hr != null && hr) {
      return Size.HUMAN;
    } else {
      return Size.B;
    }
  }

}

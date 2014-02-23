package com.cloudius.cli.util;

import java.util.ArrayList;

/**
 * A helper class to help with text formatting
 */
public class TextHelper {
  /**
   * Takes a list of lists and treats them as rows/columns. It then left-pads the columns.
   * It considers the first row to be the header row.
   *
   * For example, passing this list:
   *
   *     head1, head2, head3
   *     12983732, 892, 18293729
   *
   * Will result in:
   *
   *        head1, head2,    head3
   *     12983732,   892, 18293729
   *
   * @param table The list of lists
   * @return The same table is returned, with all the items padded.
   */
  public static ArrayList<ArrayList<String>> leftPadColumns(ArrayList<ArrayList<String>> table) {
    ArrayList<String> headers = table.get(0);
    int[] widths = new int[headers.size()];

    for (ArrayList<String> row : table) {
      for (int i=0; i<row.size(); i++) {
        widths[i] = Math.max(headers.get(i).length(), row.get(i).length());
      }
    }

    for (ArrayList<String> row : table) {
      for (int i=0; i<widths.length; i++) {
        row.set(i, String.format("%" + widths[i] + "s", row.get(i)));
      }
    }

    return table;
  }
}

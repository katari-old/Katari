/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.core.ping;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;

// import org.apache.commons.io.FileUtils;

/** Gives information about the current memory usage.
 */
public class PingMemory implements PingService {

  /** Gives information about the current memory usage.
   *
   * @return the status of the memory.
   */
  public PingResult ping() {

    long heapSize = Runtime.getRuntime().totalMemory();
    long max = Runtime.getRuntime().maxMemory();

    StringBuilder message = new StringBuilder();

    message.append("Heap Size = ").append(formatSize(heapSize)).append("\n");
    message.append("Max Heap Size = ").append(formatSize(max)).append("\n");

    for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
      String name = pool.getName();
      MemoryType type = pool.getType();
      MemoryUsage usage = pool.getUsage();
      MemoryUsage peak = pool.getPeakUsage();
      message.append("Heap named '").append(name);
      message.append("' (").append(type.toString()).append(") ");
      message.append("uses ").append(formatSize(usage.getUsed()));
      message.append(" of ").append(formatSize(usage.getMax()));
      message.append(". The max memory used so far is ");
      message.append(formatSize(peak.getUsed())).append(".\n");
    }

    return new PingResult(true, message.toString());
  }

  /** The number of bytes in a kilobyte.
   */
  private static final int KB = 1024;

  /** The number of bytes in a megabyte.
   */
  private static final int MB = KB * KB;

  /** The number of bytes in a gigabyte.
   */
  private static final int GB = KB * MB;

  /** The divisor for the fraction of the memory size.
   */
  private static final int FRACTION_DIVISOR = 100;

  /** Converts a filesize to a human readable format.
   *
   * Strings are formatted as 1024: 1KB, 1023: 1,023 B, and so on.
   *
   * @param size Size to be formatted.
   *
   * @return The formatted filesize, never null.
   */
  private String formatSize(final long size) {

    long number, reminder;
    String result;

    if (size < KB) {
      result = insertSeparator(size) + " B";
    } else if (size < MB) {
      number = size / KB;
      reminder = (size * FRACTION_DIVISOR / KB) % FRACTION_DIVISOR;
      result = String.format("%s.%02d KB", insertSeparator(number), reminder);
    } else if (size < GB) {
      number = size / MB;
      reminder = (size * FRACTION_DIVISOR / MB) % FRACTION_DIVISOR;
      result = String.format("%s.%02d MB", insertSeparator(number), reminder);
    } else {
      number = size / GB;
      reminder = (size * FRACTION_DIVISOR / GB) % FRACTION_DIVISOR;
      result = String.format("%s.%02d GB", insertSeparator(number), reminder);
    }

    // Display decimal points only if needed another alternative to this
    // approach is to check before calling str.Format, and have separate cases
    // depending on whether reminder == 0 or not.
    return result.replace(".00", "");
  }

  /** The number of digits for the thousand separator.
   */
  private static final int THOUSAND_DIGITS = 3;

  /** Converts a positive number to a string while inserting separators.
   *
   * @param number A positive number to add thousands separator for.
   *
   * @return The number with thousand separators as a String.
   */
  private String insertSeparator(final long number) {
    StringBuilder result = new StringBuilder();

    result.append(String.format("%d", number));

    for (int i = result.length() - THOUSAND_DIGITS; i > 0;
        i -= THOUSAND_DIGITS) {
      result.insert(i, ",");
    }
    return result.toString();
  }
}


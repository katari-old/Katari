/* vim: set ts=2 et sw=2 cindent fo=qroca: */

package com.globant.katari.login.local.view;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.Validate;

/** Ips that are temporarily blacklisted.
 *
 * <p>IP addresses can be added to the black list, and they will remain in the
 * list for a configured period of time. After that period, the IP will be
 * removed from the blacklist.
 *
 * @author mario.roman
 */
public class IpBlacklist {

  /** The set of blacklisted IP address mapped to the task that will evict the
   * entry.
   *
   * It is never null.
   */
  private Map<String, TimerTask> ips
    = Collections.synchronizedMap(new HashMap<String, TimerTask>());

  /** The timer that will run every configured period of time removing the
   * blacklisted IP addresses, never null.
   */
  private Timer timer = new Timer(true);

  /** The period of time in milliseconds that an IP address will remain
   * blacklisted.
   *
   * It will be always greater than 0.
   */
  private long period;

  /** Marks if this ip blacklisting is enabled or not.
   *
   * The effect of disabling blacklisting is that captchas will be effectively
   * disabled. isEnabled must be true for alwaysBlacklist to be true.
   */
  private boolean isEnabled;

  /** Marks if all ips will be considered blacklisted.
   *
   * This forces captchas to be shown always. This cannot be true if isEnabled
   * is false.
   */
  private boolean alwaysBlacklist;

  /** Flags that destroy was called.
   *
   * Used for debugging purposes only. See finalize();
   */
  private boolean destroyed = false;

  /** Creates a new IpBlacklist with a given period of time that an IP will
   * remain blacklisted.
   *
   * @param blacklistDuration the period of that that an IP will remain
   * blacklisted, in milliseconds. It must be greater than 0.
   *
   * @param enable true enables blacklisting.
   *
   * @param forceBlacklist true makes all ip blacklisted, forcing the captchas
   * to be inconditionally shown. forceBlacklist can only be true if enable is
   * true.
   */
  public IpBlacklist(final long blacklistDuration, final boolean enable, final
      boolean forceBlacklist) {
    Validate.isTrue(blacklistDuration > 0,
        "The time an ip is blacklisted must be greater than 0.");
    Validate.isTrue(enable || !forceBlacklist,
        "You cannot force blacklist if captcha is disabled.");
    period = blacklistDuration;
    isEnabled = enable;
    alwaysBlacklist = forceBlacklist;
  }

  /** Blacklists an ip.
   *
   * The ip will be blacklisted for the configured amount of time, defined in
   * the constructor.
   *
   * @param ipAddress IP address to blacklist, cannot be null nor empty.
   */
  public void blacklistIp(final String ipAddress) {
    Validate.notNull(ipAddress, "The IP address cannot be null");
    Validate.notEmpty(ipAddress, "The IP address cannot be empty");
    if (isEnabled) {
      scheduleRemoveTask(ipAddress);
    }
  }

  /** Checks if the ip has been blacklisted.
   *
   * @param ipAddress IP address to check, it cannot be null.
   *
   * @return {@code true} if the Blacklist contains the given IP, {@code false}
   * otherwise.
   */
  public boolean isBlacklisted(final String ipAddress) {
    Validate.notNull(ipAddress, "The ip address cannot be null.");
    if (alwaysBlacklist) {
      return true;
    } else {
      return ips.containsKey(ipAddress);
    }
  }

  /** Destroys all the resources allocated by this IpBlacklist instance.
   *
   * Once this operation is called, this class is no longer usable. Continue
   * using this object will give unexpected results.
   *
   * This object is normally instantiated by spring, so add
   * destroy-method='destroy' to the bean declaration.
   */
  public void destroy() {
    destroyed = true;
    timer.cancel();
  }

  /** Checks that the object was destroyed and logs an error to the console
   * otherwise.
   *
   * {@inheritDoc}
   *
   * Warning: we are not calling super.finalize() here, because we just inherit
   * from Object.
   *
   * The original signature was protected void finalize() throws Throwable, but
   * it makes findbugs complain.
   */
  @Override
  protected void finalize() {
    if (!destroyed) {
      System.out.println(
          "ERROR: IpBlacklist instance not correctly destroyed.");
    }
  }

  /** Schedules a new task to remove the given IP address from the list of
   * blacklisted ips.
   *
   * This will add a new task every time that the IP fails, but it's still a
   * good enough solution for now.
   *
   * @param ipAddress IP address to remove, it cannot be null.
   */
  private synchronized void scheduleRemoveTask(final String ipAddress) {
    Validate.notNull(ipAddress, "The ip address cannot be null.");
    if (ips.containsKey(ipAddress)) {
      ips.get(ipAddress).cancel();
    }
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        ips.remove(ipAddress);
      }
    };
    timer.schedule(task, period);
    ips.put(ipAddress, task);
  }
}


package apibetaseries;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;
import java.util.ArrayList;
import java.util.List;

public class EnumAllWindowNames {

    public static List<String> getAllWindowNames() {
        final List<String> inflList = new ArrayList<>();

        User32.instance.EnumWindows(new WndEnumProc() {
            public boolean callback(int hWnd, int lParam) {
                if (User32.instance.IsWindowVisible(hWnd)) {
                    byte[] buffer = new byte[1024];
                    User32.instance.GetWindowTextA(hWnd, buffer, buffer.length);
                    String title = Native.toString(buffer);
                    inflList.add(title);
                }
                return true;
            }
        }, 0);
        
        return inflList;
    }

    public static interface WndEnumProc extends StdCallLibrary.StdCallCallback {
        boolean callback(int hWnd, int lParam);
    }

    public static interface User32 extends StdCallLibrary {
        final User32 instance = (User32) Native.loadLibrary("user32", User32.class);
        boolean EnumWindows(WndEnumProc wndenumproc, int lParam);
        boolean IsWindowVisible(int hWnd);
        void GetWindowTextA(int hWnd, byte[] buffer, int buflen);
        int GetTopWindow(int hWnd);
        int GetWindow(int hWnd, int flag);
        final int GW_HWNDNEXT = 2;
    }
}

package controladores;/*
 * Copyright (c) 2000-2017 TeamDev Ltd. All rights reserved.
 * TeamDev PROPRIETARY and CONFIDENTIAL.
 * Use is subject to license terms.
 */

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;

import javax.swing.*;
import java.awt.*;

/**
 * The sample demonstrates how to create Browser instance, embed it,
 * load HTML content from string, and display it.
 */
public class WebCamController {
    public static void main(String... args) {
        final Browser[] browser = new Browser[1];
        final BrowserView[] view = new BrowserView[1];

        JFrame frame = new JFrame("JxBrowser - Hello World");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);




        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.isMac()) {
                    BrowserCore.initialize();
                }
                browser[0] = new Browser();
                view[0] = new BrowserView(browser[0]);
                browser[0].loadURL(args[0]);
                frame.add(view[0], BorderLayout.CENTER);
            }
        });

        t.start();




        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);


    }
} 
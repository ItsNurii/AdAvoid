/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package adrover.antiads.use;

import java.awt.Color;

/**
 * Utility class that defines the color palette used throughout the application.
 * <p>
 * This class centralizes all colors for both light mode and dark mode, ensuring
 * visual consistency and easy maintenance.
 * </p>
 *
 * <p>
 * The colors are grouped by theme:
 * </p>
 * <ul>
 * <li><b>Light mode:</b> soft blue tones</li>
 * <li><b>Dark mode:</b> neutral gray tones</li>
 * </ul>
 *
 * <p>
 * This class cannot be instantiated.
 * </p>
 *
 * @author Nuria
 * @version 1.0
 */
public class AppColor {

    /* ===================== LIGHT MODE COLORS ===================== */
    /**
     * Background color used in light mode.
     */
    public static final Color LIGHT_BG = new Color(215, 230, 245);

    /**
     * Panel background color used in light mode.
     */
    public static final Color LIGHT_PANEL = new Color(235, 242, 250);

    /**
     * Button background color used in light mode.
     */
    public static final Color LIGHT_BUTTON = new Color(170, 200, 235);

    /**
     * Text color used in light mode.
     */
    public static final Color LIGHT_TEXT = new Color(20, 30, 50);

    /* ===================== DARK MODE COLORS ===================== */
    /**
     * Background color used in dark mode.
     */
    public static final Color DARK_BG = new Color(55, 55, 60);

    /**
     * Panel background color used in dark mode.
     */
    public static final Color DARK_PANEL = new Color(75, 75, 80);

    /**
     * Button background color used in dark mode.
     */
    public static final Color DARK_BUTTON = new Color(110, 110, 115);

    /**
     * Text color used in dark mode.
     */
    public static final Color DARK_TEXT = new Color(230, 230, 230);

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AppColor() {
    }
}

/*
 * RectangleSelectionTool.java
 *
 * Created on July 10, 2002, 9:53 AM
 *
 * $Id: RectangleSelectionTool.java,v 1.2 2002/10/30 15:56:33 zachrisg Exp $
 *
 * $Log: RectangleSelectionTool.java,v $
 * Revision 1.2  2002/10/30 15:56:33  zachrisg
 * Added Id and Log tags and updated javadoc.
 *
 */

package medview.visualizer.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import medview.visualizer.data.*;

/**
 * A tool for rectangular selection.
 *
 * @author  G�ran Zachrisson <zachrisg@mdstud.chalmers.se>
 */
public class RectangleSelectionTool extends Tool {
    
    /** The startpoint of the rectangle selection. */
    private Point startPoint;
    
    /** 
     * Creates a new instance of RectangleSelectionTool. 
     *
     * @param toolHandler The ToolHandler that handles the calls generated by the Tool.
     * @param component The JComponent that the Tool will observe for user actions.
     */
    public RectangleSelectionTool(ToolHandler toolHandler, JComponent component) {
        super(toolHandler, component);
    }
    
    /** 
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     *
     * @param e The event object.
     */
    public void mouseClicked(MouseEvent e) {
        int modifiers = e.getModifiers();
        int clickCount = e.getClickCount();
        Point point = e.getPoint();
        
        if (super.toolHandler == null) {
            return;
        } else if (!super.toolHandler.supportsTool(ToolBox.TOOL_POINTER)) { // _POINTER because double click should be supported by all that supports the pointer tool.
            return;
        }
        
        if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
            if (clickCount == 2) {
                super.toolHandler.doubleClick(point);
            }
        }
    }
    
    /** 
     * Invoked when a mouse button is pressed on a component and then dragged. MOUSE_DRAGGED events will continue to be delivered to the component where the drag originated until the
     * mouse button is released (regardless of whether the mouse position is within the bounds of the component).
     *
     * Due to platform-dependent Drag&Drop implementations, MOUSE_DRAGGED events may not be delivered during a native Drag&Drop operation.
     *
     * @param e The event object.
     */
    public void mouseDragged(MouseEvent e) {
        int modifiers = e.getModifiers();
        Point point = e.getPoint();
        
        if (super.toolHandler == null) {
            return;
        } else if (!super.toolHandler.supportsTool(ToolBox.TOOL_RECTANGLE_SELECT)) {
            return;
        }
        
        if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
            int x = Math.min(startPoint.x, point.x);
            int y = Math.min(startPoint.y, point.y);
            int width = Math.max(startPoint.x, point.x) - x;
            int height = Math.max(startPoint.y, point.y) - y;
            
            Rectangle r = new Rectangle(x,y,width,height);
            
            super.toolHandler.setSelectionShape(r);
        }
    }
    
    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e The event object.
     */
    public void mousePressed(MouseEvent e) {
        int modifiers = e.getModifiers();
        Point point = e.getPoint();
        
        if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
            startPoint = point;
        }
    }
    
    /** 
     * Invoked when a mouse button has been released on a component.
     *
     * @param e The event object.
     */
    public void mouseReleased(MouseEvent e) {
        int modifiers = e.getModifiers();
        Point point = e.getPoint();
        
        if (super.toolHandler == null) {
            return;
        } else if (!super.toolHandler.supportsTool(ToolBox.TOOL_RECTANGLE_SELECT)) {
            return;
        }
        
        if ((modifiers & MouseEvent.BUTTON1_MASK) != 0) {
            int x = Math.min(startPoint.x, point.x);
            int y = Math.min(startPoint.y, point.y);
            int width = Math.max(startPoint.x, point.x) - x;
            int height = Math.max(startPoint.y, point.y) - y;
            
            Rectangle r = new Rectangle(x,y,width,height);
            
            if (e.isControlDown()) {
                super.toolHandler.addShapeSelection(r);
            } else {
                super.toolHandler.shapeSelect(r);
            }
        }
    }
    
}

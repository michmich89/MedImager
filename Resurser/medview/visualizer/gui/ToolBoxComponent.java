/*
 * ToolBoxComponent.java
 *
 * Created on July 1, 2002, 3:00 PM
 *
 * $Id: ToolBoxComponent.java,v 1.7 2003/07/02 00:32:54 erichson Exp $
 *
 * $Log: ToolBoxComponent.java,v $
 * Revision 1.7  2003/07/02 00:32:54  erichson
 * Changed to FloaterComponent and added method getFloaterType().
 * Added super() to constructor.
 *
 * Revision 1.6  2002/10/30 15:56:37  zachrisg
 * Added Id and Log tags and updated javadoc.
 *
 */

package medview.visualizer.gui;

import medview.visualizer.gui.*;
import medview.visualizer.data.*;
import javax.swing.*;

/**
 * A toolbox component.
 *
 * @author  d97nix
 */
public class ToolBoxComponent extends FloaterComponent {

    /** The ToolBox associated with this ToolBoxComponent. */
    private ToolBox toolBox;
    
    /** 
     * Creates new ToolBoxComponent.
     *
     * @param tools The ToolBox to associate the ToolBoxComponent with. 
     */
    public ToolBoxComponent(ToolBox tools) {
        super();
        initComponents();        
        toolBox = tools;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        toolButtonGroup = new javax.swing.ButtonGroup();
        pointerToolButton = new javax.swing.JToggleButton();
        rectangleSelectButton = new javax.swing.JToggleButton();

        setLayout(new java.awt.GridLayout(2, 1));

        pointerToolButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/medview/visualizer/resources/icons/arrow.gif")));
        toolButtonGroup.add(pointerToolButton);
        pointerToolButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pointerToolButtonActionPerformed(evt);
            }
        });

        add(pointerToolButton);

        rectangleSelectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/medview/visualizer/resources/icons/select.gif")));
        toolButtonGroup.add(rectangleSelectButton);
        rectangleSelectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectangleSelectButtonActionPerformed(evt);
            }
        });

        add(rectangleSelectButton);

    }//GEN-END:initComponents

    private void rectangleSelectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectangleSelectButtonActionPerformed
        // Add your handling code here:
        toolBox.setTool(ToolBox.TOOL_RECTANGLE_SELECT);
    }//GEN-LAST:event_rectangleSelectButtonActionPerformed

    private void pointerToolButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointerToolButtonActionPerformed
        // Add your handling code here:         
        toolBox.setTool(ToolBox.TOOL_POINTER);
    }//GEN-LAST:event_pointerToolButtonActionPerformed
    /*
    private void rectangleSelectionButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectangleSelectionButton1ActionPerformed
        
        // Add your handling code here:
    }//GEN-LAST:event_rectangleSelectionButton1ActionPerformed

    private void pointerToolButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pointerToolButton1ActionPerformed
       
    }//GEN-LAST:event_pointerToolButton1ActionPerformed
    */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton pointerToolButton;
    private javax.swing.JToggleButton rectangleSelectButton;
    private javax.swing.ButtonGroup toolButtonGroup;
    // End of variables declaration//GEN-END:variables

    public int getFloaterType() {
        return Floater.FLOATER_TYPE_TOOLBOX;
    }
}
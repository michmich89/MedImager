/*
 *
 * $Id: PresetListPanel.java,v 1.1 2003/11/11 00:18:19 oloft Exp $
 *
 */

package medview.formeditor.components;

import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import medview.formeditor.components.*;
import medview.formeditor.data.*;
import medview.formeditor.tools.*;
import medview.formeditor.models.*;

import medview.formeditor.interfaces.*;

/**
 *
 * @author  nils
 * @version
 */
// nader 12/4 add ChangeListeners
public class PresetListPanel extends JPanel implements ListSelectionListener,ChangeListener,FocusListener  {
    
    private ActionListener  actionListener;
    private ValueTabbedPane valueTabbedPane = null;
    private PresetModel     currentPresetModel,nextPresetModel;
    private boolean         mDeleteMod;
    private DatahandlingHandler mDH                 = DatahandlingHandler.getInstance();
    
    
    /**
     * Creates new form ValuePanel
     */
    public PresetListPanel(int aHeight) {
        super();
        mDeleteMod = false;
        initComponents();
        
        setRequestFocusEnabled(false);
        
        currentPresetModel  = new PresetModel("Current");
        nextPresetModel     = new PresetModel("Next");
        
        currentPresetModel.addChangeListener(this);  // nader 12/4
        nextPresetModel.addChangeListener(this);  // nader 12/4
        
        currentValueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        nextValueList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        currentValueList.addListSelectionListener(this);
        nextValueList.addListSelectionListener(this);
        
        // I have commented these two lines out because there is no need for it. // Nils 030605
        //currentValueList.addMouseListener(this);// to catch right click
        //nextValueList.addMouseListener(this);// to catch right click
        
        // setPreferredSize(new Dimension(130,0));
        
        // valueSplitPane.setDividerLocation(aHeight); // nader  8/4   Must be reltaed to the main win
        currentValueList.addFocusListener(this);
        valueSplitPane.setDividerLocation(0.5);
        // System.out.println("Preset List new metohd  ");
    }
    
    public void focusGained(java.awt.event.FocusEvent p1) {
        // System.out.println("FOCUS GOT PRESET LIST");
    }
    /*
     * Call the input tool to add new valur to the translator.
     */
    public void focusLost(java.awt.event.FocusEvent ev) {
        //System.out.println("FOCUS LOSTTTTT PRESET LIST");
        
    }
    
    /**
     * Move the divider of the presetlistpanel to an appropriate place
     */
    private void adjustDivider() {
        /*
        int currentPresetCount = currentPresetModel.getPresets().length + 1; // +1 for the label
        int nextPresetCount = nextPresetModel.getPresets().length + 1;  // ditto
        int totalPresets = currentPresetCount + nextPresetCount;
         
         
            valueSplitPane.setDividerLocation(  ((double) currentPresetCount) / ( (double) totalPresets));
         */
        valueSplitPane.setDividerLocation(0.5);
    }
    
    public void addActionListener(ActionListener newListener) {
        actionListener = AWTEventMulticaster.add(actionListener,newListener);
    }
    
    /**
     * Fire action event notifying preset selection
     */
    public void fireActionEvent(String value) {
        
        if (actionListener != null ) {
            actionListener.actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,value));
        }
    }
    
    
    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        valueSplitPane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        currentPresetPanel = new javax.swing.JPanel();
        currentPresetLabel = new javax.swing.JLabel();
        currentValueList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        nextPresetPanel = new javax.swing.JPanel();
        nextPresetLabel = new javax.swing.JLabel();
        nextValueList = new javax.swing.JList();

        setLayout(new java.awt.BorderLayout());

        setMinimumSize(new java.awt.Dimension(64, 46));
        setPreferredSize(new java.awt.Dimension(150, 40));
        setAutoscrolls(true);
        valueSplitPane.setBorder(null);
        valueSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        valueSplitPane.setResizeWeight(0.5);
        valueSplitPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                keyPressedOnLists(evt);
            }
        });

        jScrollPane1.setBorder(null);
        currentPresetPanel.setLayout(new java.awt.BorderLayout());

        currentPresetLabel.setText("Current preset");
        currentPresetLabel.setAlignmentX(0.5F);
        currentPresetPanel.add(currentPresetLabel, java.awt.BorderLayout.NORTH);

        currentValueList.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        currentValueList.setRequestFocusEnabled(false);
        currentValueList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressOnUpperList(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mouseReleasedOnList(evt);
            }
        });
        currentValueList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                listMouseDragged(evt);
            }
        });

        currentPresetPanel.add(currentValueList, java.awt.BorderLayout.CENTER);

        jScrollPane1.setViewportView(currentPresetPanel);

        valueSplitPane.setTopComponent(jScrollPane1);

        jScrollPane2.setBorder(null);
        nextPresetPanel.setLayout(new java.awt.BorderLayout());

        nextPresetLabel.setText("Next preset");
        nextPresetPanel.add(nextPresetLabel, java.awt.BorderLayout.NORTH);

        nextValueList.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 1, 1, 1)));
        nextValueList.setRequestFocusEnabled(false);
        nextValueList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                pressOnUpperList(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mouseReleasedOnList(evt);
            }
        });
        nextValueList.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                listMouseDragged(evt);
            }
        });

        nextPresetPanel.add(nextValueList, java.awt.BorderLayout.CENTER);

        jScrollPane2.setViewportView(nextPresetPanel);

        valueSplitPane.setRightComponent(jScrollPane2);

        add(valueSplitPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    // both mousePressed and mouseReleased must be checked for popup trigger since it is different on different platforms
    private void mouseReleasedOnList(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mouseReleasedOnList
        JList aList = (JList) evt.getSource();
        Point p = evt.getPoint();
        if(evt.isPopupTrigger()) {           
            // System.out.println("Is popup trigger");            
            new DeleteTermPopupMenu(aList, p).showMe();
        } 
    }//GEN-LAST:event_mouseReleasedOnList

    /**
     * When mouse is dragged (with button pushed down) on a list, selection
     * should not occur, so we consume the drag event and clear the selection
     */
    private void listMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listMouseDragged
        evt.consume(); // Stop this from happening
        Object source = evt.getSource();
        if (source instanceof JList) {
            ((JList) source).clearSelection();
        }
    }//GEN-LAST:event_listMouseDragged

    private void pressOnUpperList(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pressOnUpperList
        // Add your handling code here:
        mouseClickOnList(evt);
    }//GEN-LAST:event_pressOnUpperList
        
    private void keyPressedOnLists(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyPressedOnLists
        // Add your handling code here:
        int aCod = evt.getKeyCode();
        if(aCod == evt.VK_DELETE){
            boolean mDeleteFlag = true;
            // Ut.prt("f�lag = true aCod  = " + aCod +  "  del = "  + evt.VK_DELETE);
        }
    }//GEN-LAST:event_keyPressedOnLists
        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentPresetLabel;
    private javax.swing.JPanel currentPresetPanel;
    private javax.swing.JList currentValueList;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel nextPresetLabel;
    private javax.swing.JPanel nextPresetPanel;
    private javax.swing.JList nextValueList;
    private javax.swing.JSplitPane valueSplitPane;
    // End of variables declaration//GEN-END:variables
    
    
    /**
     * Set the new Mask.
     * The Mask is used to determine which presets are visible. Only presets that begin with the mask are shown.
     */
    public void setMask(String in_mask) {
        //System.out.println("Setmask(" + in_mask + ") called!");
        
        String mask = new String(in_mask);
        String[] currentPresets = currentPresetModel.getPresets(); // To handle them as an array...
        
        //The following paragraph is a hack because of a bug I haven't been able to find yet.
        if (mask.startsWith("CURRENT_ROW")) {
            //System.out.println("Got Current Row Does Not Exist. Blanking mask.");
            mask = "";
        }
        // This only affects the "current" jlist.
        Vector unmasked = new Vector();
        int masklength = mask.length();
        /* implement: adjust this part for ? values later */
        for (int i = 0; i < currentPresets.length; i++ ) {
            if ( masklength == 0) { // If mask is zero length, always add
                unmasked.add(currentPresets[i]);
            } else if (masklength <= ( (currentPresets[i]).length())) { // If masklength is longer, don't consider it
                // Check if the first part matches
                String firstPart = currentPresets[i].substring(0,masklength);
                // if (mask.equals(firstPart)) {  Nader 27/5 make it o case sensetive
                if (mask.equalsIgnoreCase(firstPart)) {
                    unmasked.add(currentPresets[i]);
                }
            }
        }
        currentValueList.setListData(unmasked);
    }
    
    
    public void setValueTabbedPane(ValueTabbedPane pane) {
        valueTabbedPane = pane;
    }
    
    
    /**
     * Make the lists display the values for the current and the next field.
     */
    
    private void setPresets(PresetModel presetModel) {
        
        if (presetModel == null) {
            //System.out.println("Setpreset(null)");
            currentPresetModel = new PresetModel("-"); // Empty JList since there are no presets
        } else {
            //System.out.println("Setpresets called, amount: " + presetModel.getPresets().length);
            //currentValueList.setListData(field.getPresets());
            currentPresetModel = presetModel;
        }
        currentPresetModel.addChangeListener(this);
        currentPresetLabel.setText(currentPresetModel.getTitle() );
        
        setMask(""); // Update presetlist contents
        revalidate();
    }
    
    private void setNextPresets(PresetModel in_nextPresetModel) {
        if( in_nextPresetModel == null) {
            //System.out.println("SetNextPresets(null");
            nextPresetModel = new PresetModel("-");
        } else {
            //System.out.println("SetNextPresets(n�nting)");
            nextPresetModel = in_nextPresetModel;
        }
        
        nextPresetModel.addChangeListener(this);
        nextPresetLabel.setText(nextPresetModel.getTitle());
        nextValueList.setListData(nextPresetModel.getPresets());
        // revalidate etc
    }
    
    /**
     * Listens for stateChanged events (to change list contents) from a ValueTabbedPane
     */
    
    public void stateChanged(javax.swing.event.ChangeEvent e) {// nader 12/4
        updatePresets(e.getSource() );
    }
    
    public void updatePresets(Object source) {
        
        // System.out.println("PresetLists update");
        if (source instanceof ValueTabbedPane) {
            
            // This is executed when the tab is changed in ValueTabbedPane
            ValueTabbedPane sourceTabbedPane = (ValueTabbedPane) source;
            
            TabPanel activeTab = sourceTabbedPane.getSelectedTab();
            
            if (activeTab == null) {
                //System.out.println("Active tab == null, blanking presets");
                setPresets(null); // No presets
            } else {
                //System.out.println("Active tab ok");
                
                ValueInputComponent activeComponent = activeTab.getSelectedInputComponent();
                
                if (activeComponent == null) {
                    //System.out.println("Active component is null, setting current presets to None");
                    setPresets(new PresetModel("None"));
                }
                else {
                    setPresets(activeComponent.getPresetModel());
                }
                ValueInputComponent nextComponent = sourceTabbedPane.getNextInputComponent();
                if (nextComponent == null) {
                    //System.out.println("nextComponent is null, forcing next presets to be None");
                    setNextPresets(new PresetModel("None"));
                }
                else {
                    setNextPresets(nextComponent.getPresetModel());
                }
                //adjustDivider();
            }
        }
        else if(source instanceof PresetModel) {
            //System.out.println("instance of presetModel");
        }
        
        else{
            System.out.println("ValueListPanel.stateChanged: error: event not from ValueTabbedPane!");
            this.updateUI(); // nader 12/4
        }
    }
    
    
    /**
     * Listens for ListSelection events from both JLists.
     * Was used to fire actions to the listener (valueTabbedPane)
     * Call as ListSelectionListener
     * 
     * 
     * This has been removed (commented out), instead the functionality is in mouseClickOnList()
     * so that we can tell left and right mouse clicks apart and act accordingly / Nils 03-06-05
     */
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        //  if (e.getValueIsAdjusting() == false) { // Skip the first event if we get two
        //  int     firstChanged    = e.getFirstIndex();
        /*
        Object  aSource = e.getSource();
        if(!mDeleteMod){ // If delete button is inactive
            addFromLists(aSource); // Then add item
        }
        else{ // Delete button is active so remove it
            if(aSource instanceof JList){
                JList   aList   = (JList) aSource;
                int     index   = aList.getSelectedIndex();
                if(index < 0) return;
                removeFormList(aList,index);
                mChBxDelete.setSelected(false);
                mDeleteMod = false;
            }
        }
         */
    }
    
    private void addFromLists(Object  source){
        
        String  whichList       = "ERROR-LIST_UNKNOWN";
        String  value           = "ERROR-VALUE_NOT_SET";
        
        // Which JList is the origin of the click?
        if ((source == currentValueList) || (source == nextValueList)) {
            JList sourceList = (JList) source;
            
            if (sourceList == currentValueList) {
                whichList = "THIS";
            }
            else if (sourceList == nextValueList) {
                whichList = "NEXT";
            }
            Object val = sourceList.getSelectedValue();
            if (val instanceof String) {
                value = (String) val;
                // Fire action event
                this.fireActionEvent( whichList + "=" + value);
            }
        }
        else if (source == nextValueList) { 
            JList sourceList = (JList) source;
        }
        
    }

    

   
   /**
     * Listens for ListSelection events from both JLists., fires actions to the listener (valueTabbedPane)
     * Call as ListSelectionListener  
     */
    /*public void valueChanged(javax.swing.event.ListSelectionEvent e) {
        callfromLists(e);
    }
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {// to catch right click
        // this.mouseClickOnList(mouseEvent);
    }
    private void callfromLists(javax.swing.event.ListSelectionEvent e) {
        
        //  if (e.getValueIsAdjusting() == false) { // Skip the first event if we get two
        //  int     firstChanged    = e.getFirstIndex();
        Object  aSource = e.getSource();
        if(!mDeleteMod){
            addFromLists(aSource);
        }
        else{
            if(aSource instanceof JList){
                JList   aList   = (JList) aSource;
                int     index   = aList.getSelectedIndex();
                if(index < 0) return;
                removeFormList(aList,index);
                mChBxDelete.setSelected(false);
                mDeleteMod = false;
            }
        }
    } 
   */

    
     /**
     * Called via the GUI when a mouse is clicked on one of the presetListPanels.
     * If it is a left click, the value will be added to the current field (if it doesn't already exist). The selection is then cleared.
     * If it is a right click, there will be an option to remove that preset value. 
     * 
     */
    public void mouseClickOnList(java.awt.event.MouseEvent e) {

        Object aSource = e.getSource(); // Get source of the click
        if (aSource == null) return; // If the source is null, stop here (should not happen)
        if(aSource instanceof JList){
            Point p = e.getPoint();
            JList   aList   = (JList) aSource;                         
            
            if(e.isPopupTrigger()) {                
                // removeFormList(aList,index); // Ask the user whether to remove this value
                new DeleteTermPopupMenu(aList, e.getPoint()).showMe();
            } else {
                if (e.getButton() == MouseEvent.BUTTON1) { // Only left clicks should select (necessary since right clicks aren't always popup triggers (depends on platform)) // Nils
                    int index = aList.locationToIndex(p);
                    if (index >= 0) // Check that the mouse click gives a valid location, i.e not -1
                        addFromList(aList,aList.locationToIndex(p)); // Add this value to the current field
                    aList.clearSelection(); // always clear the selection after a left mouse click.
                }
            }
        }
    }    
    
    
    
    /**
     * A popup menu with the option to delete a certain term, used when right clicking on a term
     */    
    private class DeleteTermPopupMenu extends JPopupMenu  {
        JList termList;
        Point p;
        int termIndex;
        
        public DeleteTermPopupMenu(JList component, Point position) {
            super();                        
            termList = component;
            p = position;
            this.termIndex = component.locationToIndex(position);
       
            String termName = termList.getModel().getElementAt(termIndex).toString();            
            setLabel(termName); // Not shown on windows platform
                                                
            JMenuItem removeItem = new JMenuItem("Ta bort " + termName);
            removeItem.setMnemonic('T');                                    
            removeItem.addActionListener(new ActionListener() {        
                // Action to be taken if removeItem is clicked        
                public void actionPerformed(ActionEvent e) {
                    removeFormList(termList, termIndex);
                    setVisible(false);            
                }
            });
                               
            add(removeItem);
        }
         
        // Pop up at the appropriate location
        public void showMe() {
            this.show(termList,p.x,p.y);
        }                
        
        
        
        
    }
    
    
    
    private void addFromList(JList pList,int index){
        
        String  whichList       = "ERROR-LIST_UNKNOWN";
        String  value           = "ERROR-VALUE_NOT_SET";
        
        // Get the value at index
        Object valueObject = pList.getModel().getElementAt(index);
        
        if (valueObject == null) {
            return; // Do nothing if no selected value
        }
        
        if (valueObject instanceof String) {
            value = (String) valueObject;
        
            pList.setSelectedIndex(index); // Not necessary
            
            String  aVal    = (String)pList.getSelectedValue(); // Get the selected value from the JList

            if (pList == currentValueList) {
                whichList = "THIS";
            }
            else if (pList == nextValueList) {
                whichList = "NEXT";
            }
            else return; // Unknown source, should not happen.

            // Fire action event
            //System.out.println("Chose value " + value); // Debug
            this.fireActionEvent( whichList + "=" + value); // Tell listeners that there was a value selection
        } else {
            System.err.println("medrecords.components.PresetListPanel addFromLists error: Clicked object not instance of string");
        }
    }
    
    private void removeFormList(JList pList,int index) {

        if(index < 0)             
            return; // Error, end method call here        
        
        pList.setSelectedIndex(index);
        String  aVal    = (String)pList.getSelectedValue();
        if (aVal == null){
            return;
        }
        // Ut.prt("Call remove " + aVal );
        PresetModel     aModel  = null;
        if(pList == currentValueList)
            aModel       = currentPresetModel;
        else aModel      = nextPresetModel;
        
        String   aTerm   = aModel.getTermName() ;
        int ans = Ut.yesNoQuestion(mDH.getLanguageString(mDH.QUESTION_SHOULD_REMOVE_VALUE_LS_PROPERTY) +" " + aVal +"?");
        if(ans == Ut.Yes){
            mDH.removeValue(aTerm,aVal);
            // pList.removeSelectionInterval(index,index);
            aModel.remove(aVal);
            //aModel.fireStateChanged();
            pList.setListData(aModel.getPresets());
            //setMask("");
            // revalidate();
        }
    }           
}

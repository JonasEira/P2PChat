/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainWindow.java
 *
 * Created on 2011-sep-10, 23:57:54
 */
package GUIComponents;

import Connection.ChatException;
import Connection.ConnPoint;
import Connection.DataTypes;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Jonas
 */
public class MainWindow extends javax.swing.JFrame implements DataWatcher {
    Configuration _conf;
    private SoundControl _testSound;
    ConnectWindow _conn;
    JPopupMenu optionMenu;
    ButtonGroup group;
            
    /** Creates new form MainWindow */
    public MainWindow() {
        initComponents();
        this.setTitle("ChatTest");
        this.setVisible(true);
        _conn = null;
        _conf = new Configuration();
        
        group = new ButtonGroup();
        group.add(freq220);
        group.add(freq320);
        group.add(freq441);
        group.setSelected(freq220.getModel(), true);
        Configuration.centerWindow(this);
        
    }
    public void setTab(int i){
        jTabbedPane1.setSelectedIndex(i);
    }
    public void addText(String s){
        chatArea.append(s);
    }
    
    public void flashStatus(String s){
        Object statusLock = statusLabel.getTreeLock();
        synchronized(statusLock) {
            statusLabel.setText(s);
            Thread t = new Thread() {
            @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                    }
                    statusLabel.setText("");

                }
            };
            t.start();
        }
        
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        chatPanel = new javax.swing.JPanel();
        chatScrollPane = new javax.swing.JScrollPane();
        chatArea = new javax.swing.JTextArea();
        inputField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        test = new javax.swing.JButton();
        test2 = new javax.swing.JButton();
        userTableScrollPane = new javax.swing.JScrollPane();
        userTable = new javax.swing.JTable();
        statusLabel = new javax.swing.JLabel();
        _menubar = new javax.swing.JMenuBar();
        _mainMenu = new javax.swing.JMenu();
        _connItem = new javax.swing.JMenuItem();
        _enableSelfCheckMenuItem = new javax.swing.JCheckBoxMenuItem();
        _exitItem = new javax.swing.JMenuItem();
        _optionMenu = new javax.swing.JMenu();
        sampleFrequencyOptions = new javax.swing.JMenu();
        freq441 = new javax.swing.JRadioButtonMenuItem();
        freq320 = new javax.swing.JRadioButtonMenuItem();
        freq220 = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        chatPanel.setLayout(new java.awt.BorderLayout());

        chatArea.setColumns(20);
        chatArea.setEditable(false);
        chatArea.setRows(5);
        chatScrollPane.setViewportView(chatArea);

        chatPanel.add(chatScrollPane, java.awt.BorderLayout.CENTER);

        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                inputFieldKeyPressed(evt);
            }
        });
        chatPanel.add(inputField, java.awt.BorderLayout.PAGE_START);

        test.setText("Start");
        test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });
        jPanel1.add(test);

        test2.setText("Stop");
        test2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });
        jPanel1.add(test2);

        chatPanel.add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jTabbedPane1.addTab("Chat", chatPanel);

        userTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Local Port", "Mode", "Remote Port", "Address"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        userTable.setColumnSelectionAllowed(true);
        userTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                userTableMouseReleased(evt);
            }
        });
        userTableScrollPane.setViewportView(userTable);
        userTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jTabbedPane1.addTab("UserTable", userTableScrollPane);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        statusLabel.setText("-");
        getContentPane().add(statusLabel, java.awt.BorderLayout.SOUTH);

        _mainMenu.setText("Main");

        _connItem.setText("Add User");
        _connItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _connItemActionPerformed(evt);
            }
        });
        _mainMenu.add(_connItem);

        _enableSelfCheckMenuItem.setSelected(true);
        _enableSelfCheckMenuItem.setText("Enable own Mic");
        _enableSelfCheckMenuItem.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                _enableSelfCheckMenuItemStateChanged(evt);
            }
        });
        _mainMenu.add(_enableSelfCheckMenuItem);

        _exitItem.setText("Exit");
        _exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _exitItemActionPerformed(evt);
            }
        });
        _mainMenu.add(_exitItem);

        _menubar.add(_mainMenu);

        _optionMenu.setText("Options");

        sampleFrequencyOptions.setText("Sample Frequency");

        freq441.setSelected(true);
        freq441.setText("44100 hz");
        freq441.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freq441ActionPerformed(evt);
            }
        });
        sampleFrequencyOptions.add(freq441);

        freq320.setSelected(true);
        freq320.setText("32000 hz");
        freq320.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freq320ActionPerformed(evt);
            }
        });
        sampleFrequencyOptions.add(freq320);

        freq220.setSelected(true);
        freq220.setText("22050 hz");
        freq220.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freq220ActionPerformed(evt);
            }
        });
        sampleFrequencyOptions.add(freq220);

        _optionMenu.add(sampleFrequencyOptions);

        _menubar.add(_optionMenu);

        setJMenuBar(_menubar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _connItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__connItemActionPerformed
        if(_conn != null){
            _conn.dispose();
            _conn = null;
        }
        _conn = new ConnectWindow(this);
        _conn.requestFocusInWindow();
        _conn.requestFocus();
        
    }//GEN-LAST:event__connItemActionPerformed
    
    public void refreshList(){
        Object[] allConnections = _conf.getAllConnections();
        TableColumnModel cm = userTable.getColumnModel();
        Object[] labels = new Object[cm.getColumnCount()];
        
        for (int k = 0; k < cm.getColumnCount(); k++){
            labels[k] = cm.getColumn(k).getHeaderValue();
        }
        
        userTable.removeAll();
        TableModel _tm = new DefaultTableModel(labels, allConnections.length);
        
        for(int n=0; n < allConnections.length; n++){
            _tm.setValueAt(((ConnPoint)allConnections[n]).getName(), n, 0);
            _tm.setValueAt(((ConnPoint)allConnections[n]).getLocalPort(), n, 1);
            _tm.setValueAt(((ConnPoint)allConnections[n]).getMode().name(), n, 2);
            _tm.setValueAt(((ConnPoint)allConnections[n]).getRemotePort(), n, 3);
            _tm.setValueAt(((ConnPoint)allConnections[n]).getRemotePoint().getCanonicalHostName(), n, 4);
        }
        userTable.setModel(_tm);
    }
    
    private void _exitItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__exitItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event__exitItemActionPerformed

    private void inputFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyPressed
        if ( evt.getKeyChar() == KeyEvent.VK_ENTER){
            Object[] allConnections = _conf.getAllConnections();
            for(int n=0; n < allConnections.length; n++){
                try {
                    ((ConnPoint)allConnections[n]).sendText(inputField.getText());
                } catch (ChatException ex) {
                    ex.printStackTrace();
                }
            }
            chatArea.append("Me: " + inputField.getText()+"\n");
            inputField.setText(null);
        }
        
    }//GEN-LAST:event_inputFieldKeyPressed

    private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
        if(_testSound == null){
            _testSound = new SoundControl(_conf);
            _testSound.setEnabled(_enableSelfCheckMenuItem.getState());
            Object[] allConnections = _conf.getAllConnections();
            for(int n=0; n < allConnections.length; n++){
                ConnPoint c = ((ConnPoint)allConnections[n]);    
                c.addDataWatcher(_testSound.getPlayer());
                _testSound.getCapturer().addSoundWatcher(c);
                
            }
        } else {
            JOptionPane.showMessageDialog(this, "Already started.", "Already started.", JOptionPane.OK_OPTION);
        }
        
    }//GEN-LAST:event_startActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed
        if(_testSound != null){
            _testSound.stopRecording();
            Object[] allConnections = _conf.getAllConnections();
            for(int n=0; n < allConnections.length; n++){
                ConnPoint c = ((ConnPoint)allConnections[n]); 
                c.removeDataWatcher(_testSound.getPlayer());
                _testSound.getCapturer().removeSoundWatcher(c);
            }
            _testSound = null;
        } else {
            JOptionPane.showMessageDialog(this, "We haven't started yet.", "Not started", JOptionPane.OK_OPTION);
        }
    }//GEN-LAST:event_stopActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        if(jTabbedPane1.getModel().getSelectedIndex() == 1){
            
            this.refreshList();
        }
    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void userTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_userTableMouseReleased
        if(userTable.getRowCount() > 0 && evt.getButton() == MouseEvent.BUTTON3){
            int row = userTable.rowAtPoint(evt.getPoint());
            PopupMenu p = new PopupMenu();
            p.show(userTable, evt.getX(), evt.getY());
        }
        
        
    }//GEN-LAST:event_userTableMouseReleased

    private void _enableSelfCheckMenuItemStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event__enableSelfCheckMenuItemStateChanged
        if(_testSound != null){
            _testSound.setEnabled(_enableSelfCheckMenuItem.getState());
        }
    }//GEN-LAST:event__enableSelfCheckMenuItemStateChanged

    private void freq441ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freq441ActionPerformed
        _conf.setSamplingFrequency(44100);
    }//GEN-LAST:event_freq441ActionPerformed

    private void freq320ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freq320ActionPerformed
        _conf.setSamplingFrequency(32000);
    }//GEN-LAST:event_freq320ActionPerformed

    private void freq220ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freq220ActionPerformed
        _conf.setSamplingFrequency(22050);
    }//GEN-LAST:event_freq220ActionPerformed
   
    private class PopupMenu extends JPopupMenu implements ActionListener {
        JMenuItem conn;
        JMenuItem listen;
        JMenuItem disconnect;
                
        public PopupMenu() {
            conn = new JMenuItem("Connect");
            listen = new JMenuItem("Listen");
            disconnect = new JMenuItem("Disconnect");
            conn.addActionListener(this);
            listen.addActionListener(this);
            disconnect.addActionListener(this);
            this.add(conn);
            this.add(listen);
            this.add(disconnect);
            
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            if(ae.getActionCommand().equals("Connect")){
                _conf.getConnPoint(userTable.getModel().getValueAt(userTable.rowAtPoint(this.getLocation()),0).toString()).connect();
            }
            if(ae.getActionCommand().equals("Disconnect")){
                _conf.getConnPoint(userTable.getModel().getValueAt(userTable.rowAtPoint(this.getLocation()),0).toString()).close();
            }
            if(ae.getActionCommand().equals("Listen")){
                _conf.getConnPoint(userTable.getModel().getValueAt(userTable.rowAtPoint(this.getLocation()),0).toString()).listen();
            }
        }
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem _connItem;
    private javax.swing.JCheckBoxMenuItem _enableSelfCheckMenuItem;
    private javax.swing.JMenuItem _exitItem;
    private javax.swing.JMenu _mainMenu;
    private javax.swing.JMenuBar _menubar;
    private javax.swing.JMenu _optionMenu;
    private javax.swing.JTextArea chatArea;
    private javax.swing.JPanel chatPanel;
    private javax.swing.JScrollPane chatScrollPane;
    private javax.swing.JRadioButtonMenuItem freq220;
    private javax.swing.JRadioButtonMenuItem freq320;
    private javax.swing.JRadioButtonMenuItem freq441;
    private javax.swing.JTextField inputField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenu sampleFrequencyOptions;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JButton test;
    private javax.swing.JButton test2;
    private javax.swing.JTable userTable;
    private javax.swing.JScrollPane userTableScrollPane;
    // End of variables declaration//GEN-END:variables

    public Configuration getConfiguration() {
        return this._conf;
    }
    
    public SoundControl getSoundControl() {
        return this._testSound;
    }
    
    @Override
    public void fireDataThrough(Object o, int typeOfData){
        if(typeOfData == DataTypes.TEXT_ID) {
//            byte[] data = (byte[]) o;
            String s = (String)o;
//            for(int n=0; n < data.length; n+=2){
//                int theCharValue = ((int)data[n] << 8) + data[n+1];
//                char theChar = (char)theCharValue;
//                s = s + theChar;
//            }
            this.addText(s+"\n");
        }
    }
}

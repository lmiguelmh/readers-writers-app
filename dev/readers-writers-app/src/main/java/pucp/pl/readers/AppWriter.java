/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pucp.pl.readers;

import javax.swing.DefaultListModel;
import javax.swing.ListModel;

/**
 *
 * @author adun
 */
public class AppWriter extends Thread {
    DefaultListModel model;
    AppResource resource;
    
    AppWriter(AppResource resource, DefaultListModel writersListModel) {
        model = writersListModel;
        this.resource = resource;
        log("new writer added");
    }

    void log(String message) {
        this.model.addElement(message);
    }
}

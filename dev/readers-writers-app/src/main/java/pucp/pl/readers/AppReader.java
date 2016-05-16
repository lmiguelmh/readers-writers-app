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
public class AppReader extends Thread {
    DefaultListModel model;
    AppResource resource;

    AppReader(AppResource resource, DefaultListModel readersListModel) {
        this.model = readersListModel;
        this.resource = resource;
        log("new reader added");
    }
    
    void log(String message) {
        this.model.addElement(message);
    }
}

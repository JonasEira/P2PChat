/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package p2pchat;

import java.io.PrintWriter;
import javax.sound.sampled.*;



/**
 *
 * @author Jonas
 */
class ListMixers {
    static PrintWriter out;
    
    static void listAll(final PrintWriter out) {
        ListMixers.out = out;
        Mixer.Info[] aInfos = AudioSystem.getMixerInfo();
        for (int i = 0; i < aInfos.length; i++) {
            try {
                Mixer mixer = AudioSystem.getMixer(aInfos[i]);
                out.println(""+i+": "+aInfos[i].getName()+", "
                        +aInfos[i].getVendor()+", "
                        +aInfos[i].getVersion()+", "
                        +aInfos[i].getDescription());
                
                printLines(mixer, mixer.getSourceLineInfo());
                printLines(mixer, mixer.getTargetLineInfo());
            } catch (Exception e) {
                out.println("Exception: "+e);
            }
            out.println();
        }
        if (aInfos.length == 0) {
            out.println("[No mixers available]");
        }
    }
    
    static void printLines(Mixer mixer, Line.Info[] infos) {
        for (int i = 0; i < infos.length; i++) {
            try {
                if (infos[i] instanceof Port.Info) {
                    Port.Info info = (Port.Info) infos[i];
 
                    out.println("  Port " + info);
                }
                if (infos[i] instanceof DataLine.Info) {
                    DataLine.Info info = (DataLine.Info) infos[i];
 
                    out.println("  Line " + info + " (max. " +
                                mixer.getMaxLines(info) + " simultaneously): ");
                    printFormats(info);
                }
                Line line = mixer.getLine(infos[i]);
 
                if (!(line instanceof Clip)) {
                    try {
                        line.open();
                    }
                    catch (LineUnavailableException e) {
                        out.println("LineUnavailableException when trying to open this line");
                    }
                }
                try {
                    printControls(line.getControls());
                }
                finally {
                    if (!(line instanceof Clip)) {
                        line.close();
                    }
                }
            }
            catch (Exception e) {
                out.println("Exception: " + e);
            }
            out.println();
        }
    }
    
    static void printFormats(DataLine.Info info) {
        AudioFormat[] formats = info.getFormats();
        for (int i = 0; i < formats.length; i++) {
            out.println("  "+i+": "+formats[i]
                    +" ("+formats[i].getChannels()+" channels, "
                    +"frameSize="+formats[i].getFrameSize()+", "
                    +(formats[i].isBigEndian()?"big endian":"little endian")
                    +")");
        }
        if (formats.length == 0) {
            out.println("  [no formats]");
        }
        out.println();
    }
    
    static void printControls(Control[] controls) {
        for (int i = 0; i<controls.length; i++) {
            printControl("    ", "Controls["+i+"]: ", controls[i]);
        }
        if (controls.length == 0) {
            out.println("    [no controls]");
        }
        out.println();
    }
    
    static void printControl(String indent, String id, Control control) {
        if (control instanceof BooleanControl) {
            BooleanControl ctrl = (BooleanControl) control;
            out.println(indent+id+"BooleanControl: "+ctrl);
        } else if (control instanceof CompoundControl) {
            CompoundControl ctrl = (CompoundControl) control;
            Control[] ctrls = ctrl.getMemberControls();
            out.println(indent+id+"CompoundControl: "+control);
            for (int i=0; i<ctrls.length; i++) {
                printControl(indent+"  ", "MemberControls["+i+"]: ", ctrls[i]);
            }
        } else if (control instanceof EnumControl) {
            EnumControl ctrl = (EnumControl) control;
            Object[] values = ctrl.getValues();
            Object value = ctrl.getValue();
            out.println(indent+id+"EnumControl: "+control);
            for (int i=0; i<values.length; i++) {
                if (values[i] instanceof Control) {
                    printControl(indent+"  ", "Values["+i+"]: "+((values[i]==value)?"*":""), (Control) values[i]);
                } else {
                    out.println(indent+"  Values["+i+"]: "+((values[i]==value)?"*":"")+values[i]);
                }
            }
        } else if (control instanceof FloatControl) {
            FloatControl ctrl = (FloatControl) control;
            out.println(indent+id+"FloatControl: "+ctrl);
        } else {
            out.println(indent+id+"Control: "+control);
        }
    }   
}

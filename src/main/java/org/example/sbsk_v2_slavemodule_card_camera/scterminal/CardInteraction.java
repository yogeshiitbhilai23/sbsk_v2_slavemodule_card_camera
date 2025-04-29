/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.example.sbsk_v2_slavemodule_card_camera.scterminal;

import javax.smartcardio.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author radcolor
 */

public class CardInteraction {
    public static class TerminalNotFoundException extends Exception {}
    public static class InstructionFailedException extends Exception {}
    public static class ByteCastException extends Exception {}

    
    public static CardTerminals getCardTerminals() throws CardException, TerminalNotFoundException {
        TerminalFactory factory = TerminalFactory.getDefault();
//        System.out.println("Type: "+factory.getType() +" and Provider: "+factory.getProvider().getInfo());
        //TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
        CardTerminals terminals = factory.terminals();
        if ((terminals == null) || (terminals.list().isEmpty())) {
            throw new TerminalNotFoundException();
        }
        return terminals;  
    }
    
//    static Card connectToCard1(CardTerminal terminal) throws CardException {
//        // wait for card, indefinitely until card appears
//        if (terminal.isCardPresent()) {
//            // establish a connection to the card using autoselected protocol
//            Card card = terminal.connect("*");
//            // obtain logical channel
//            return card;
//        }            
//        return null;
//    }
    
    public static Card connectToCard(CardTerminal terminal) throws CardException {
        // wait for card, indefinitely until card appears
//        if (terminal.isCardPresent() == false) {
//            System.out.println("*** Insert card");
            if (terminal.waitForCardPresent(0)) {                
                // establish a connection to the card using autoselected protocol
                Card card = terminal.connect("*");
                // obtain logical channel
                return card;
            }
//        }
    return null;
    }
    
    public static Map<String, String> readFile(CardChannel channel) throws CardException, InstructionFailedException, ByteCastException {
        // To Select Binary file 3F04
        byte[] select3F04Cmd = {
            (byte) 0x00, //CLA
            (byte) 0xA4, //INS
            (byte) 0x00, //P1
            (byte) 0x04, //P2
            (byte) 0x02, //Lc
            (byte) 0x3F, //File Identifier
            (byte) 0x04,
            (byte) 0x02 //Le
        };
        ResponseAPDU answer = channel.transmit(new CommandAPDU(select3F04Cmd));
        if (answer.getSW() != 0x9000) {
            throw new InstructionFailedException();
        }
        // To Read Binary file 3F04
        byte[] read3F04Cmd = {
            (byte) 0x00, //CLA
            (byte) 0xB0, //INS
            (byte) 0x00, //P1
            (byte) 0x00, //P2
            (byte) 0x00 //Le (0x50)
        };
        answer = channel.transmit(new CommandAPDU(read3F04Cmd));
        if (answer.getSW() != 0x9000) {
            throw new InstructionFailedException();
        }
        byte [] personData = answer.getData();
//        System.out.printf("1. Data in file 3F04: %s%n", hexify(personData));
        ///
        //byte[] personData = {(byte)0x30, (byte)0x82, (byte)0x0F, (byte)0xBE, (byte)0x02, (byte)0x01, (byte)0x01, (byte)0x13, (byte)0x08, (byte)0x33, (byte)0x31, (byte)0x39, (byte)0x30, (byte)0x30, (byte)0x31, (byte)0x32, (byte)0x30, (byte)0x13, (byte)0x0A, (byte)0x32, (byte)0x39, (byte)0x2F, (byte)0x30, (byte)0x32, (byte)0x2F, (byte)0x32, (byte)0x30, (byte)0x32, (byte)0x34, (byte)0x0C, (byte)0x15, (byte)0x4D, (byte)0x72, (byte)0x20, (byte)0x52, (byte)0x75, (byte)0x64, (byte)0x72, (byte)0x61, (byte)0x20, (byte)0x44, (byte)0x75, (byte)0x74, (byte)0x74, (byte)0x61, (byte)0x20, (byte)0x54, (byte)0x69, (byte)0x77, (byte)0x61, (byte)0x72, (byte)0x69, (byte)0x13, (byte)0x0A, (byte)0x30, (byte)0x38, (byte)0x2F, (byte)0x30, (byte)0x31, (byte)0x2F, (byte)0x31, (byte)0x39, (byte)0x38, (byte)0x30, (byte)0x13, (byte)0x0C, (byte)0x72, (byte)0x75, (byte)0x64, (byte)0x72, (byte)0x61, (byte)0x2E, (byte)0x74, (byte)0x69, (byte)0x77, (byte)0x61, (byte)0x72, (byte)0x69, (byte)0x0C, (byte)0x0A, (byte)0x37, (byte)0x34, (byte)0x32, (byte)0x38, (byte)0x36, (byte)0x33, (byte)0x31, (byte)0x36, (byte)0x35, (byte)0x31, (byte)0x13, (byte)0x0A, (byte)0x4F, (byte)0x20, (byte)0x50, (byte)0x6F, (byte)0x73, (byte)0x69, (byte)0x74, (byte)0x69, (byte)0x76, (byte)0x65, (byte)0x13, (byte)0x15, (byte)0x44, (byte)0x65, (byte)0x61, (byte)0x6E, (byte)0x2C, (byte)0x20, (byte)0x46, (byte)0x61, (byte)0x63, (byte)0x75, (byte)0x6C, (byte)0x74, (byte)0x79, (byte)0x20, (byte)0x41, (byte)0x66, (byte)0x66, (byte)0x61, (byte)0x69, (byte)0x72, (byte)0x73, (byte)0x13, (byte)0x04, (byte)0x4D, (byte)0x61, (byte)0x6C, (byte)0x65, (byte)0x13, (byte)0x0A, (byte)0x31, (byte)0x33, (byte)0x2F, (byte)0x30, (byte)0x33, (byte)0x2F, (byte)0x32, (byte)0x30, (byte)0x31, (byte)0x39, (byte)0x80, (byte)0x18, (byte)0x53, (byte)0x65, (byte)0x6E, (byte)0x69, (byte)0x6F, (byte)0x72, (byte)0x20, (byte)0x43, (byte)0x6F, (byte)0x6D, (byte)0x70, (byte)0x75, (byte)0x74, (byte)0x65, (byte)0x72, (byte)0x0A, (byte)0x45, (byte)0x6E, (byte)0x67, (byte)0x69, (byte)0x6E, (byte)0x65, (byte)0x65, (byte)0x72, (byte)0x81, (byte)0x00, (byte)0x82, (byte)0x00, (byte)0x83, (byte)0x00, (byte)0x84, (byte)0x00, (byte)0x85, (byte)0x00, (byte)0x86, (byte)0x0A, (byte)0x30, (byte)0x31, (byte)0x2F, (byte)0x30, (byte)0x33, (byte)0x2F, (byte)0x32, (byte)0x30, (byte)0x31, (byte)0x39, (byte)0x87, (byte)0x00, (byte)0x04, (byte)0x82, (byte)0x0E, (byte)0xFD, (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0, (byte)0x00, (byte)0x10, (byte)0x4A, (byte)0x46, (byte)0x49, (byte)0x46, (byte)0x00, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x60, (byte)0x00, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xDB, (byte)0x00, (byte)0x43, (byte)0x00, (byte)0x08, (byte)0x06, (byte)0x06, (byte)0x07, (byte)0x06, (byte)0x05, (byte)0x08, (byte)0x07, (byte)0x07, (byte)0x07, (byte)0x09, (byte)0x09, (byte)0x08, (byte)0x0A, (byte)0x0C, (byte)0x14, (byte)0x0D, (byte)0x0C, (byte)0x0B, (byte)0x0B, (byte)0x0C, (byte)0x19, (byte)0x12, (byte)0x13, (byte)0x0F, (byte)0x14, (byte)0x1D, (byte)0x1A, (byte)0x1F, (byte)0x1E, (byte)0x1D, (byte)0x1A, (byte)0x1C, (byte)0x1C};
       ///
        Map<String, String> map = new HashMap<String, String>();
        int startIdx = 8;
        byte len = personData[startIdx];
        byte [] idNo = new byte[len];
        for(int i = 0; i < len; i++){
            idNo[i] = personData[++startIdx];
        }
        map.put("IDNUMBER", new String(idNo));
        
        startIdx += 2;
        len = personData[startIdx];
        byte [] validUpto = new byte[len];
        for(int i = 0; i < len; i++){
            validUpto[i] = personData[++startIdx];
        }
        map.put("VALIDUPTO", new String(validUpto));
        
        startIdx += 2;
        len = personData[startIdx];
        byte [] name = new byte[len];
        for(int i = 0; i < len; i++){
            name[i] = personData[++startIdx];
        }
        map.put("NAME", new String(name));
        
        return map;
    }
        
    public static void disconnectCard(Card card) throws CardException{
        card.disconnect(false);
    }
    
    public static String hexify(Byte[] bytes) {
        ArrayList<String> bytesStrings = new ArrayList<String>(bytes.length);
        for (byte b : bytes) {
            bytesStrings.add(String.format("%02X", b));
        }
        return String.join(" ", bytesStrings);
    }
    
    public static String hexify(byte[] bytes) {
        ArrayList<String> bytesStrings = new ArrayList<String>(bytes.length);
        for (byte b : bytes) {
            bytesStrings.add(String.format("%02X", b));
        }
        return String.join(" ", bytesStrings);
    }
    

    public static byte[] toByteArray(int[] list) throws ByteCastException {
        int s = list.length;
        byte[] buf = new byte[s];
        for (int i=0; i<s; i++) {
            if (i < 0 || i > 255) {
                throw new ByteCastException();
            }
            buf[i] = (byte)list[i];
        }
        return buf;
    }
    
}
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.example.sbsk_v2_slavemodule_card_camera.scterminal;

import javax.smartcardio.*;
import java.util.Map;

public class Scterminal {
    public static void main(String[] args) {
        try {
            // Get the card terminals
            CardTerminals terminals = CardInteraction.getCardTerminals();

            // Get the first terminal
            CardTerminal terminal = terminals.list().get(0);

            // Connect to the card
            Card card = CardInteraction.connectToCard(terminal);
            if (card == null) {
                System.out.println("No card found in the terminal.");
                return;
            }

            // Get the card channel
            CardChannel channel = card.getBasicChannel();

            // Read data from the card
            Map<String, String> cardData = CardInteraction.readFile(channel);
            System.out.println(cardData.get("NAME"));
            System.out.println("\n");
            System.out.println(cardData.get("IDNUMBER"));

            //Printing all the data present in the card:
//            System.out.println("All Card Data:");
//            for (Map.Entry<String, String> entry : cardData.entrySet()) {
//                System.out.println(entry.getKey() + ": " + entry.getValue());
//            }
            // Disconnect the card
            CardInteraction.disconnectCard(card);
        } catch (CardInteraction.TerminalNotFoundException e) {
            System.err.println("No card terminal found.");
        } catch (CardException e) {
            System.err.println("Card exception: " + e.getMessage());
        } catch (CardInteraction.InstructionFailedException e) {
            System.err.println("Failed to execute card instruction.");
        } catch (CardInteraction.ByteCastException e) {
            System.err.println("Failed to cast byte array.");
        }
    }

    public static boolean isCardPresent() {
        try {
            // Get the card terminals
            CardTerminals terminals = CardInteraction.getCardTerminals();

            // Get the first terminal
            if (terminals.list().isEmpty()) {
                System.out.println("No card terminals found.");
                return false;
            }
            CardTerminal terminal = terminals.list().get(0);

            // Check if the card is present
            return terminal.isCardPresent();
        } catch (CardException | CardInteraction.TerminalNotFoundException e) {
            System.err.println("Error checking card presence: " + e.getMessage());
            return false;
        }
    }

    public static String getCardData(int CARD_READ_TIMEOUT_MS) throws Exception {
        try {
            // Check if the card is present
            if (!isCardPresent()) {
                return "No card detected in the terminal.";
            }

            // Get the card terminals
            CardTerminals terminals = CardInteraction.getCardTerminals();

            // Get the first terminal
            CardTerminal terminal = terminals.list().get(0);

            // Connect to the card
            Card card = CardInteraction.connectToCard(terminal);
            if (card == null) {
                return "No card found in the terminal.";
            }

            // Get the card channel
            CardChannel channel = card.getBasicChannel();

            // Read data from the card
            Map<String, String> cardData = CardInteraction.readFile(channel);
            String idNumber = cardData.get("IDNUMBER");
            String name = cardData.get("NAME");

            // Disconnect the card
            CardInteraction.disconnectCard(card);

//            return idNumber != null ? idNumber : "ID Number not found.";

            // Return both ID Number and Name
            String result = "ID Number: " + (idNumber != null ? idNumber : "ID Number not found.") +
                    ", Name: " + (name != null ? name : "Name not found.");
            return result;
        } catch (CardInteraction.TerminalNotFoundException e) {
            return "No card terminal found.";
        } catch (CardException e) {
            return "Card exception: " + e.getMessage();
        } catch (CardInteraction.InstructionFailedException e) {
            return "Failed to execute card instruction.";
        } catch (CardInteraction.ByteCastException e) {
            return "Failed to cast byte array.";
        }
    }


}

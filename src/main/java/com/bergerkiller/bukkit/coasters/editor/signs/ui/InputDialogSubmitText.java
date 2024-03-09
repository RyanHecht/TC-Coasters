package com.bergerkiller.bukkit.coasters.editor.signs.ui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.bergerkiller.bukkit.coasters.TCCoasters;
import com.bergerkiller.bukkit.common.wrappers.ChatText;

/**
 * Based on MapWidgetSubmitText<br>
 * <br>
 * Implementation of {@link InputDialogAnvil} geared towards allowing
 * the player to input text. To display the input, activate the widget.
 * When the player submits text, {@link #onAccept(text)} is called.
 * When the player cancels, {@link #onCancel()} is called.
 */
public class InputDialogSubmitText extends InputDialogAnvil {
    private boolean _accepted = false;

    public InputDialogSubmitText(TCCoasters plugin, Player player) {
        super(plugin, player);
        LEFT_BUTTON.setMaterial(Material.BARRIER);
        LEFT_BUTTON.setDescription(ChatColor.RED + "Cancel");
        RIGHT_BUTTON.setMaterial(Material.EMERALD);
        RIGHT_BUTTON.setDescription(ChatColor.GREEN + "Accept");
    }

    /**
     * Called when the player clicks on the accept button,
     * confirming his choice of text
     * 
     * @param text accepted
     */
    public void onAccept(String text) {
    }

    /**
     * Called when the player closes the window, without pressing
     * the accept button.
     */
    public void onCancel() {
    }

    /**
     * Sets the description displayed to the user, explaining
     * what this dialog is about.
     * 
     * @param text
     */
    public InputDialogSubmitText setDescription(String text) {
        if (text == null || text.isEmpty()) {
            MIDDLE_BUTTON.setMaterial(null);
        } else {
            MIDDLE_BUTTON.setMaterial(Material.PAPER);
            MIDDLE_BUTTON.setTitle(ChatColor.YELLOW + "About");
            MIDDLE_BUTTON.setDescription(text);
        }
        return this;
    }

    @Override
    public ChatText getTitle() {
        String desc = MIDDLE_BUTTON.getDescription();
        if (desc != null && !desc.isEmpty()) {
            return ChatText.fromMessage(desc);
        } else {
            return super.getTitle();
        }
    }

    @Override
    public void onClick(Button button) {
        if (button == RIGHT_BUTTON) {
            if (this.getText() != null && !this.getText().isEmpty()) {
                this._accepted = true;
                this.close();
            }
        } else if (button == LEFT_BUTTON) {
            this._accepted = false;
            this.close();
        }
    }

    @Override
    public void onClose() {
        if (this._accepted) {
            this.onAccept(this.getText());
        } else {
            this.onCancel();
        }
    }

    @Override
    public void open() {
        super.open();
        this._accepted = false;
    }
}

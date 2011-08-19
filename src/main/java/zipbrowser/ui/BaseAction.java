/**
 * 
 */
package zipbrowser.ui;

import javax.swing.AbstractAction;
import javax.swing.Action;

abstract class BaseAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
    public BaseAction(String name, int mnemonic) {
        super.putValue(Action.NAME, name);
        super.putValue(Action.MNEMONIC_KEY, mnemonic);
    }
}
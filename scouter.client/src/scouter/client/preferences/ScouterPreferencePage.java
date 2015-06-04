/*
 *  Copyright 2015 LG CNS.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); 
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *
 */
package scouter.client.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import scouter.Version;
import scouter.client.Activator;
import scouter.client.Images;
import scouter.client.M;
import scouter.client.server.ServerManager;
import scouter.client.util.ColorUtil;
import scouter.client.util.RCPUtil;
import scouter.client.util.UIUtil;
import scouter.lang.counters.CounterConstants;
import scouter.lang.counters.CounterEngine;
import scouter.util.CastUtil;
import scouter.util.ObjectUtil;


public class ScouterPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	ComboFieldEditor serverIP;
	
	Combo /*addrCombo,*/ javaeeCombo, databaseCombo;
	
	Text file, color, maxText,  updatePathText, alertDialogTimeout;
	
	String filePath = "";
	String colorRgb = "";
	
	private String javaee;
	private String db;
	
	private int maxBlock;
	
	String updateServerPath = "";
	
	int alertdialogTimeoutSec = -1;
	
	public ScouterPreferencePage() {
		super();
		noDefaultAndApplyButton();
		setDescription(M.PREFERENCE_EXPAND);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		
//		addrList = new ArrayList<String>(Arrays.asList(ServerPrefUtil.getSvrAddrArrayFromPreference(PreferenceConstants.P_SVR_ADDRESSES)));
//		addrLoginList = new ArrayList<String>(Arrays.asList(ServerPrefUtil.getSvrAddrArrayFromPreference(PreferenceConstants.P_SVR_LOGIN_ADDRESSES)));
	}
	
	@Override
	protected Control createContents(Composite parent) {
		
		((GridLayout)parent.getLayout()).marginBottom = 30;
		
		Label versionLabel = new Label(parent, SWT.NONE);
		versionLabel.setText(" - Current Version : "+Version.getClientFullVersion());
		versionLabel.setLayoutData(UIUtil.gridData(SWT.FILL));
		
	
	    
		// ----Default Object Type----
		Group layoutGroup = new Group(parent, SWT.NONE);
	    layoutGroup.setText("Default Object Type");
		layoutGroup.setLayout(UIUtil.formLayout(5, 5));
		layoutGroup.setLayoutData(UIUtil.gridData(SWT.FILL));
	    
		CounterEngine counterEngine = ServerManager.getInstance().getDefaultServer().getCounterEngine();
		
		javaeeCombo = new Combo(layoutGroup, SWT.VERTICAL| SWT.BORDER |SWT.H_SCROLL);
		javaeeCombo.setItems(counterEngine.getChildren(CounterConstants.FAMILY_JAVAEE));
		javaeeCombo.setText(javaee);
		javaeeCombo.setEnabled(true);
		javaeeCombo.setLayoutData(UIUtil.formData(null, -1, 0, 8, 100, -5, null, -1, 220));
		
		CLabel javaLabel = new CLabel(layoutGroup, SWT.NONE);
		javaLabel.setText("default \'JavaEE\'");
		javaLabel.setImage(Images.getObjectIcon(CounterConstants.JAVA, true, 0));
		javaLabel.setLayoutData(UIUtil.formData(null, -1, 0, 8, javaeeCombo, -5, null, -1, 130));
		
	    databaseCombo = new Combo(layoutGroup, SWT.VERTICAL| SWT.BORDER |SWT.H_SCROLL);
	    databaseCombo.setItems(counterEngine.getChildren(CounterConstants.FAMILY_DATABASE));
	    databaseCombo.setText(db);
	    databaseCombo.setEnabled(true);
		databaseCombo.setLayoutData(UIUtil.formData(null, -1, javaeeCombo, 8, 100, -5, null, -1, 220));
		
		CLabel dbLabel = new CLabel(layoutGroup, SWT.NONE);
		dbLabel.setText("default \'DB\'");
		dbLabel.setImage(Images.getObjectIcon(CounterConstants.FAMILY_DATABASE, true, 0));
		dbLabel.setLayoutData(UIUtil.formData(null, -1, javaLabel, 8, databaseCombo, -5, null, -1, 130));
				
		// ----Mass Profiling----
		layoutGroup = new Group(parent, SWT.NONE);
	    layoutGroup.setText("Profiling");
		layoutGroup.setLayout(UIUtil.formLayout(5, 5));
		layoutGroup.setLayoutData(UIUtil.gridData(SWT.FILL));
		
		maxText = new Text(layoutGroup, SWT.BORDER | SWT.RIGHT);
		maxText.setText(""+maxBlock);
		maxText.setBackground(ColorUtil.getInstance().getColor("white"));
		maxText.setLayoutData(UIUtil.formData(null, -1, 0, -2, 100, -5, null, -1, 265));
		maxText.addVerifyListener(new VerifyListener() { // for number only input.
	        public void verifyText(VerifyEvent e) {
	            Text text = (Text)e.getSource();
	            final String oldS = text.getText();
	            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
	            boolean isFloat = true;
				try {
					Float.parseFloat(newS);
				} catch (NumberFormatException ex) {
					isFloat = false;
				}
	            if(!isFloat)
	                e.doit = false;
	        }
	    });
		
		Label label = new Label(layoutGroup, SWT.NONE);
        label.setText("Max Block count:");
		label.setLayoutData(UIUtil.formData(null, -1, null, -1, maxText, -5, null, -1, 100));
		
		
		// ----Update----
		layoutGroup = new Group(parent, SWT.NONE);
	    layoutGroup.setText("Update Server");
		layoutGroup.setLayout(UIUtil.formLayout(5, 5));
		layoutGroup.setLayoutData(UIUtil.gridData(SWT.FILL));
		
		updatePathText = new Text(layoutGroup, SWT.BORDER);
		updatePathText.setText(updateServerPath);
		updatePathText.setBackground(ColorUtil.getInstance().getColor(SWT.COLOR_WHITE));
		updatePathText.setLayoutData(UIUtil.formData(null, -1, 0, -2, 100, -5, null, -1, 265));
		
		Label updatePathLabel = new Label(layoutGroup, SWT.NONE);
		updatePathLabel.setText("Update Server path:");
		updatePathLabel.setLayoutData(UIUtil.formData(null, -1, null, -1, updatePathText, -5, null, -1));
		
		// ----Update----
		layoutGroup = new Group(parent, SWT.NONE);
	    layoutGroup.setText("Alert");
		layoutGroup.setLayout(UIUtil.formLayout(5, 5));
		layoutGroup.setLayoutData(UIUtil.gridData(SWT.FILL));
		
		Label alertDialogTimeoutLabel = new Label(layoutGroup, SWT.NONE | SWT.RIGHT);
		alertDialogTimeoutLabel.setText("Set alert dialog timeout in seconds. \'-1\' will not destroy dialog.");
		alertDialogTimeoutLabel.setLayoutData(UIUtil.formData(null, -1, null, -1, 100, -5, null, -1));
		
		Label secLbl = new Label(layoutGroup, SWT.NONE);
		secLbl.setText("sec.");
		secLbl.setLayoutData(UIUtil.formData(null, -1, alertDialogTimeoutLabel, 7, 100, -5, null, -1, 40));
		
		alertDialogTimeout = new Text(layoutGroup, SWT.BORDER | SWT.RIGHT);
		alertDialogTimeout.setText(""+alertdialogTimeoutSec);
		alertDialogTimeout.setBackground(ColorUtil.getInstance().getColor(SWT.COLOR_WHITE));
		alertDialogTimeout.setLayoutData(UIUtil.formData(null, -1, alertDialogTimeoutLabel, 5, secLbl, -5, null, -1, 220));
		
		return super.createContents(parent);
	}

	public void init(IWorkbench workbench) {
		javaee = PManager.getInstance().getString(PreferenceConstants.P_PERS_WAS_SERV_DEFAULT_WAS);
		db = PManager.getInstance().getString(PreferenceConstants.P_PERS_WAS_SERV_DEFAULT_DB);
		maxBlock = PManager.getInstance().getInt(PreferenceConstants.P_MASS_PROFILE_BLOCK);
		
		updateServerPath = PManager.getInstance().getString(PreferenceConstants.P_UPDATE_SERVER_ADDR);
		
		alertdialogTimeoutSec = PManager.getInstance().getInt(PreferenceConstants.P_ALERT_DIALOG_TIMEOUT);
	}
	
	@Override
	public boolean performOk() {

		boolean needResetPerspective = false;
		
		if (!ObjectUtil.equals(javaee, javaeeCombo.getText())
				|| !ObjectUtil.equals(db, databaseCombo.getText())) {
			needResetPerspective = true;
		}
		
		if (needResetPerspective 
				&& !MessageDialog.openConfirm(getShell(), "Reset Perspectives", "To apply \'Default Object Type\', all perspectives will be reset. Continue?")) {
			return false;
		}
		
		PManager.getInstance().setValue(PreferenceConstants.P_PERS_WAS_SERV_DEFAULT_WAS, javaeeCombo.getText());
		PManager.getInstance().setValue(PreferenceConstants.P_PERS_WAS_SERV_DEFAULT_DB, databaseCombo.getText());
		
		PManager.getInstance().setValue(PreferenceConstants.P_MASS_PROFILE_BLOCK, CastUtil.cint(maxText.getText()));
		
		PManager.getInstance().setValue(PreferenceConstants.P_UPDATE_SERVER_ADDR, updatePathText.getText());
		
		PManager.getInstance().setValue(PreferenceConstants.P_ALERT_DIALOG_TIMEOUT, CastUtil.cint(alertDialogTimeout.getText()));
		
		if (needResetPerspective) {
			RCPUtil.resetPerspective();
		}
		return true;
	}
	
	protected void createFieldEditors() {
	}
}
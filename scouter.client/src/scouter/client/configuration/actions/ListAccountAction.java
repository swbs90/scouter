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
package scouter.client.configuration.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import scouter.client.Images;
import scouter.client.configuration.views.AccountListView;
import scouter.client.util.ImageUtil;


public class ListAccountAction extends Action {
	public final static String ID = ListAccountAction.class.getName();

	private final IWorkbenchWindow window;
	int serverId;
	
	public ListAccountAction(IWorkbenchWindow window, int serverId) {
		this.window = window;
		this.serverId = serverId;
		setText("List Accounts");
		setId(ID);
	}

	public void run() {
		if (window != null) {
			try {
				window.getActivePage().showView(AccountListView.ID, "" + serverId, IWorkbenchPage.VIEW_ACTIVATE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}
}

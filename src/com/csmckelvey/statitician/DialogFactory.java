package com.csmckelvey.statitician;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ListView;

public class DialogFactory {
	
	public DialogFactory() {}
	
	public void prepareDashboardBack(AlertDialog.Builder builder) {
		builder.setMessage("GAME IN PROGRESS - Really Want To Quit?");
		builder.setTitle(null);
		builder.setView(null);
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });
	}

	public void prepareShotDialog(AlertDialog.Builder builder) {
		builder.setTitle("Shot Type");
		shotfoulNullify(builder);
	}
	
	public void prepareFoulDialog(AlertDialog.Builder builder) {
		builder.setTitle("Foul Type");
		shotfoulNullify(builder);
	}
	
	public void prepareVerifyDialog(AlertDialog.Builder builder) {
		builder.setMessage("Submit This Game?");
		builder.setTitle(null);
		builder.setView(null);
		builder.setAdapter(null, null);
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });
	}
	
	public void prepareListViewDialog(AlertDialog.Builder builder) {
		builder.setView(null);
		builder.setTitle(null);
		builder.setAdapter(null, null);
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });
	}

	public void prepareClearListDialog(AlertDialog.Builder builder) {
		builder.setView(null);
		builder.setTitle(null);
		builder.setAdapter(null, null);
		builder.setMessage("CLEAR THE WHOLE LIST OF STATS?");
		builder.setNegativeButton("NO", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });
	}
	
	public void showFeedbackListDialog(AlertDialog.Builder builder, ListView v) {
		builder.setView(v);
		builder.setTitle(null);
		builder.setMessage(null);
		builder.setAdapter(null, null);
		builder.setPositiveButton(null, null);
		builder.setNegativeButton("DONE", new DialogInterface.OnClickListener() { public void onClick(DialogInterface dialog, int id) { dialog.cancel(); } });
		builder.show();
	}
	
	public void shotfoulNullify(AlertDialog.Builder builder) {
		builder.setView(null);
		builder.setMessage(null);
		builder.setNegativeButton(null, null);
		builder.setPositiveButton(null, null);
	}
}

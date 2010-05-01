/*
 * BillCalculator for C345 P03
 * (c) 2010 Faruq Rasid
 * YOU ARE FREE TO MODIFY/CUT/CHOP/BOMB THIS CODE AS
 * I RECEIVE A CREDIT/MENTION SOMEWHERE. STRICTLY FOR
 * EDUCATIONAL PURPOSES ONLY AND NOT FOR SALE/RESALE/SCAM.
 * 
 * About layout:
 * 	BillCalculator uses a 'Frame-based' layout which resizes
 * 	when the IME pops out. It's a ScrollView + Toolbar (LinearLayout) enclosed
 * 	in a LinearLayout. Rational for this UI design is that even in limited
 * 	space, i.e. IME is visible, the user can interact with the UI with minimal
 * 	compromise - form can be scrolled to be filled up, calculate button is always
 * 	visible for use. The aim is to have a simple, clean & intuitive layout through
 * 	examining human process of thoughts.
 * 
 * Other things for note:
 * 	- Layout are mostly made up of LinearLayout for easy manipulation and visualization for the coder
 * 	- Spacings are managed to ensure comfortability to the user's eyes
 * 	- UI is FULLY localized
 *	- Use of TextView as labels are minimized (especially for EditText)
 *	- Spinner is made up of 2 arrays: 1 for the labels, 1 for the values (see code below to understand)
 */
package sg.ruqqq.BillCalculator;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class BillCalculator extends Activity {
    private EditText vBillAmount, vBillPax;
    private Spinner vDiscount;
    private CheckBox vServiceCharge, vGstCharge, vCessCharge;
    private Button vCalculate, vClear;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Get bill information views from layout
        vBillAmount = (EditText) findViewById(R.id.editBill);
        vBillPax = (EditText) findViewById(R.id.editPax);
        
        // Get vDiscount from layout
        vDiscount = (Spinner) findViewById(R.id.spinDiscount);
        // Set List Items for vDiscount
        ArrayAdapter discountAdapter = ArrayAdapter.createFromResource(
        								this,
        								R.array.discount_types_labels,
        								android.R.layout.simple_spinner_item);
        discountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vDiscount.setAdapter(discountAdapter);
        
        // Get calculation options views from layout
        vServiceCharge = (CheckBox) findViewById(R.id.checkServiceCharge);
        vGstCharge = (CheckBox) findViewById(R.id.checkGSTCharge);
        vCessCharge = (CheckBox) findViewById(R.id.checkCessCharge);
        
        // Get vCalculate from layout
        vCalculate = (Button) findViewById(R.id.btnCalculate);
        
        // Set function of Calculate button
        vCalculate.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		calculateBill(); // call BillCalculator calculateBill method
           	}
        });
        
        // Get vClear
        vClear = (Button) findViewById(R.id.btnClear);
        // Set 'clear' function
        vClear.setOnClickListener(new OnClickListener() {
           	public void onClick(View v) {
           		clearInputs(); // call BillCalculator clearInputs method
           	}
        });
    }
    
    public void clearInputs() {
    	// Create dialog box for request confirmation
    	new AlertDialog.Builder(this)
		.setTitle(R.string.clear_text)
		.setMessage(R.string.clear_message)
		.setPositiveButton(R.string.clear_text, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// if confirmed...
				vBillAmount.setText("");
           		vBillPax.setText("");
           		vDiscount.setSelection(0);
           		vServiceCharge.setChecked(false);
           		vGstCharge.setChecked(false);
           		vCessCharge.setChecked(false);
				return;
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
    		public void onClick(DialogInterface dialog, int whichButton) {
    			// if canceled...
    			return;
			}
    	})
		.show();
    }
    
    public void calculateBill() {
    	double totalBill = 0.0;
    	
    	// Error checking codes (Prevents FC); On error popup dialog and stop execution of method
    	// 		Check for BillAmount not empty
    	if (vBillAmount.getText().toString().length() == 0) {
    		new AlertDialog.Builder(this)
			.setTitle(R.string.error)
			.setMessage(R.string.error_invalid_amount)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			})
			.show();
    		return;
    	}
    	
    	//		Check for BillPax not empty
    	if (vBillPax.getText().toString().length() == 0) {
    		new AlertDialog.Builder(this)
			.setTitle(R.string.error)
			.setMessage(R.string.error_invalid_pax)
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			})
			.show();
    		return;
    	}
    	
    	// Get numbers from widgets
    	double bill = Double.parseDouble(vBillAmount.getText().toString());
    	int pax = Integer.parseInt(vBillPax.getText().toString());
    	// Get discount percentage from discount_types_values array
    	double discountPercentage = getResources().getIntArray(R.array.discount_types_values)[vDiscount.getSelectedItemPosition()];
    	
    	// Calculate!
    	totalBill = bill = bill-(bill*(discountPercentage/100));
    	if (vServiceCharge.isChecked()) totalBill += bill*0.1;
    	if (vGstCharge.isChecked()) totalBill += bill*0.07;
    	if (vCessCharge.isChecked()) totalBill += bill*0.01;
    	
    	double eachBill = totalBill/pax; // Each person's bill
    	
    	// Decimal Formatting
    	DecimalFormat moneyFormat = new DecimalFormat("###,###.###"); // Format decimal - prettifier!
    	
    	// Create popup dialog to show result (with a Close button
    	new AlertDialog.Builder(this)
			.setTitle(R.string.calculate_title)
			.setMessage(getString(R.string.calculate_message, moneyFormat.format(totalBill), moneyFormat.format(eachBill)))
			.setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					return;
				}
			})
			.show();
    	
    	return;
    }
}
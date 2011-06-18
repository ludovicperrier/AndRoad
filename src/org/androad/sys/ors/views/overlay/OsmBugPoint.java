package org.androad.sys.ors.views.overlay;

import org.osmdroid.util.GeoPoint;

import org.androad.R;
import org.androad.sys.osb.adt.OpenStreetBug;

import android.content.Context;
import android.graphics.Color;

public class OsmBugPoint extends CircleItem {

	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

    private OpenStreetBug bug;

	// ===========================================================
	// Constructors
	// ===========================================================

	public OsmBugPoint(final OpenStreetBug bug, final Context ctx) {
        super(bug, ctx, bug.isOpen() ? Color.RED : Color.GREEN, bug.getDesription());
        this.bug = bug;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}

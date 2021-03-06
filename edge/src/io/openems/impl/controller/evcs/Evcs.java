/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016, 2017 FENECON GmbH and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   FENECON GmbH - initial API and implementation and initial documentation
 *******************************************************************************/
package io.openems.impl.controller.evcs;

import io.openems.api.channel.ReadChannel;
import io.openems.api.channel.WriteChannel;
import io.openems.api.controller.IsThingMap;
import io.openems.api.controller.ThingMap;
import io.openems.api.device.nature.evcs.EvcsNature;

@IsThingMap(type = EvcsNature.class)
public class Evcs extends ThingMap {

	public final ReadChannel<Integer> userCurrent;
	public final WriteChannel<Integer> setCurrent;
	public final WriteChannel<Boolean> setEnabled;

	public Evcs(EvcsNature evcs) {
		super(evcs);
		this.setCurrent = evcs.setCurrent();
		this.setEnabled = evcs.setEnabled();
		this.userCurrent = evcs.currUser();
	}

}

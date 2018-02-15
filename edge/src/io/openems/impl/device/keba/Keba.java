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
package io.openems.impl.device.keba;

import java.net.Inet4Address;
import java.util.HashSet;
import java.util.Set;

import io.openems.api.bridge.Bridge;
import io.openems.api.channel.ConfigChannel;
import io.openems.api.device.nature.DeviceNature;
import io.openems.api.doc.ChannelInfo;
import io.openems.api.doc.ThingInfo;
import io.openems.common.exceptions.OpenemsException;
import io.openems.impl.protocol.keba.KebaDevice;

/*
 * Example config:
 *
 * <pre>
 * // TODO
 * </pre>
 */

@ThingInfo(title = "KEBA KeContact")
public class Keba extends KebaDevice {

	/*
	 * Constructors
	 */
	public Keba(Bridge parent) throws OpenemsException {
		super(parent);
	}

	/*
	 * Config
	 */
	@ChannelInfo(title = "IP", description = "IP address of the Keba EVCS.", type = Inet4Address.class)
	public ConfigChannel<Inet4Address> ip = new ConfigChannel<Inet4Address>("ip", this);

	@ChannelInfo(title = "evcs", description = "Sets the EVCS nature.", type = KebaEvcs.class)
	public final ConfigChannel<KebaEvcs> evcs = new ConfigChannel<KebaEvcs>("evcs", this).addChangeListener(this);

	/*
	 * Methods
	 */
	@Override
	protected Set<DeviceNature> getDeviceNatures() {
		Set<DeviceNature> natures = new HashSet<>();
		if (evcs.valueOptional().isPresent()) {
			natures.add(evcs.valueOptional().get());
		}
		return natures;
	}
}

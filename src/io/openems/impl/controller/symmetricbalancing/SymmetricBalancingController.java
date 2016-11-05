/*******************************************************************************
 * OpenEMS - Open Source Energy Management System
 * Copyright (c) 2016 FENECON GmbH and contributors
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
package io.openems.impl.controller.symmetricbalancing;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import io.openems.api.controller.Controller;
import io.openems.api.controller.IsThingMapping;
import io.openems.api.device.nature.ess.SymmetricEssNature;
import io.openems.api.exception.InvalidValueException;
import io.openems.api.exception.WriteChannelException;

/*
 * this Controller calculates the power consumption of the house and charges or discharges the storages to reach zero power consumption from the grid
 */
public class SymmetricBalancingController extends Controller {
	@IsThingMapping public List<Ess> esss = null;

	@IsThingMapping public Meter meter;

	private boolean isOnGrid() {
		for (Ess ess : esss) {
			Optional<String> gridMode = ess.gridMode.labelOptional();
			if (gridMode.isPresent() && !gridMode.get().equals(SymmetricEssNature.ON_GRID)) {
				return false;
			}
		}
		return true;
	}

	@Override public void run() {
		try {
			// Run only if all ess are on-grid
			if (isOnGrid()) {
				// Calculate required sum values
				long calculatedPower = meter.activePower.value();
				long maxChargePower = 0;
				long maxDischargePower = 0;
				long useableSoc = 0;
				for (Ess ess : esss) {
					calculatedPower += ess.activePower.value();
					maxChargePower += ess.setActivePower.writeMin().orElse(0L);
					maxDischargePower += ess.setActivePower.writeMax().orElse(0L);
					useableSoc += ess.useableSoc();
				}
				if (calculatedPower > 0) {
					/*
					 * Discharge
					 */
					if (calculatedPower > maxDischargePower) {
						calculatedPower = maxDischargePower;
					}
					// sort ess by useableSoc asc
					Collections.sort(esss, (a, b) -> {
						try {
							return (int) (a.useableSoc() - b.useableSoc());
						} catch (InvalidValueException e) {
							log.error(e.getMessage());
							return 0;
						}
					});
					for (int i = 0; i < esss.size(); i++) {
						Ess ess = esss.get(i);
						// calculate minimal power needed to fulfill the calculatedPower
						long minP = calculatedPower;
						for (int j = i + 1; j < esss.size(); j++) {
							if (esss.get(j).useableSoc() > 0) {
								minP -= esss.get(j).allowedDischarge.value();
							}
						}
						if (minP < 0) {
							minP = 0;
						}
						// check maximal power to avoid larger charges then calculatedPower
						long maxP = ess.allowedDischarge.value();
						if (calculatedPower < maxP) {
							maxP = calculatedPower;
						}
						double diff = maxP - minP;
						/*
						 * weight the range of possible power by the useableSoc
						 * if the useableSoc is negative the ess will be charged
						 */
						long p = (long) (Math.ceil((minP + diff / useableSoc * ess.useableSoc()) / 100) * 100);
						ess.setActivePower.pushWrite(p);
						ess.setReactivePower.pushWrite(0L);
						calculatedPower -= p;
					}
				} else {
					/*
					 * Charge
					 */
					if (calculatedPower < maxChargePower) {
						calculatedPower = maxChargePower;
					}
					/*
					 * sort ess by 100 - useabelSoc
					 * 100 - 90 = 10
					 * 100 - 45 = 55
					 * 100 - (- 5) = 105
					 * => ess with negative useableSoc will be charged much more then one with positive useableSoc
					 */
					Collections.sort(esss, (a, b) -> {
						try {
							return (int) ((100 - a.useableSoc()) - (100 - b.useableSoc()));
						} catch (InvalidValueException e) {
							log.error(e.getMessage());
							return 0;
						}
					});
					for (int i = 0; i < esss.size(); i++) {
						Ess ess = esss.get(i);
						// calculate minimal power needed to fulfill the calculatedPower
						long minP = calculatedPower;
						for (int j = i + 1; j < esss.size(); j++) {
							minP -= esss.get(j).allowedCharge.value();
						}
						if (minP > 0) {
							minP = 0;
						}
						// check maximal power to avoid larger charges then calculatedPower
						long maxP = ess.allowedCharge.value();
						if (calculatedPower > maxP) {
							maxP = calculatedPower;
						}
						double diff = maxP - minP;
						// weight the range of possible power by the useableSoc
						long p = (long) Math.floor((minP + diff / useableSoc * (100 - ess.useableSoc())) / 100) * 100;
						ess.setActivePower.pushWrite(p);
						calculatedPower -= p;
					}
				}

			}
		} catch (InvalidValueException | WriteChannelException e) {
			log.error(e.getMessage());
		}
	}

}
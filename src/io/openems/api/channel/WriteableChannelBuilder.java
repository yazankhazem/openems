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
package io.openems.api.channel;

public class WriteableChannelBuilder<B extends WriteableChannelBuilder<?>> extends ChannelBuilder<B> {
	protected Long maxWriteValue = null;
	protected Channel maxWriteValueChannel = null;
	protected Long minWriteValue = null;
	protected Channel minWriteValueChannel = null;

	@Override
	public WriteableChannel build() {
		return new WriteableChannel(nature, unit, minValue, maxValue, multiplier, delta, labels, minWriteValue,
				minWriteValueChannel, maxWriteValue, maxWriteValueChannel);
	}

	@SuppressWarnings("unchecked")
	public B maxWriteValue(Channel maxWriteValueChannel) {
		this.maxWriteValueChannel = maxWriteValueChannel;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B maxWriteValue(Long maxWriteValue) {
		this.maxWriteValue = maxWriteValue;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B minWriteValue(Channel minWriteValueChannel) {
		this.minWriteValueChannel = minWriteValueChannel;
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	public B minWriteValue(Long minWriteValue) {
		this.minWriteValue = minWriteValue;
		return (B) this;
	}
}
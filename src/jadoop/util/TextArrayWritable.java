// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
// This file is part of Jadoop
// Copyright (c) 2016 Grant Braught. All rights reserved.
// 
// Jadoop is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published
// by the Free Software Foundation, either version 3 of the License,
// or (at your option) any later version.
// 
// Jadoop is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty
// of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
// See the GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public
// License along with Jadoop.
// If not, see <http://www.gnu.org/licenses/>.
// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

package jadoop.util;

import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Text;

/**
 * A hadoop ArrayWritable that can hold Text objects.
 * 
 * This class is used by the HadoopGridTaskRunner (a Mapper) to receive the
 * command, and its arguments, to be run in the Mapper task.
 * 
 * This is necessary because the ArrayWritable class does not have a default
 * constructor. When hadoop attempts to send an ArrayWritable across the grid it
 * attempts to create the object on the other end using reflection and the
 * default constructor. See the java doc for the ArrayWritable class in the
 * hadoop API for an explanation.
 * 
 * @author Grant Braught
 * @author Xiang Wei
 * @author Dickinson College
 * @version July 13, 2015
 */
public class TextArrayWritable extends ArrayWritable {

	/**
	 * Construct a new empty TextArrayWritable.
	 */
	public TextArrayWritable() {
		super(Text.class);
	}

	/**
	 * Construct a new TextArrayWritable with one entry for each String in
	 * value.
	 * 
	 * @param value
	 *            an array of Strings to populate the TextArrayWritable.
	 */
	public TextArrayWritable(String[] value) {
		super(value);
	}
}

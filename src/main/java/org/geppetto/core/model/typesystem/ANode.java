/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2011 - 2015 OpenWorm.
 * http://openworm.org
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License
 * which accompanies this distribution, and is available at
 * http://opensource.org/licenses/MIT
 *
 * Contributors:
 *     	OpenWorm - http://openworm.org/people.html
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights 
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell 
 * copies of the Software, and to permit persons to whom the Software is 
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE 
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.geppetto.core.model.typesystem;


/**
 * @author matteocantarelli
 *
 */
public abstract class ANode implements INode
{

	protected String name = null;

	protected ANode parent;

	public ANode(String name)
	{
		this.name=name;
	}

	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.geppetto.core.model.typesystem.INode#getPath()
	 */
	@Override
	public String getPath()
	{
		StringBuffer fullName = new StringBuffer(this.name);
		ANode iterateState = this.getParent();
		while(iterateState != null && !(iterateState instanceof Root))
		{
			if(!fullName.toString().isEmpty())
			{
				fullName.insert(0, ".");
			}
			fullName.insert(0, iterateState.name);
			iterateState = iterateState.getParent();
		}
		return fullName.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.model.typesystem.INode#getParent()
	 */
	@Override
	public ANode getParent()
	{
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.geppetto.core.model.typesystem.INode#setParent(org.geppetto.core.model.typesystem.ANode)
	 */
	@Override
	public void setParent(ANode parent)
	{
		this.parent = parent;
	}

}

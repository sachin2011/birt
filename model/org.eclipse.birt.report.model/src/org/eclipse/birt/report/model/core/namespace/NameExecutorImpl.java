/*******************************************************************************
 * Copyright (c) 2004 Actuate Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *  Actuate Corporation  - initial API and implementation
 *******************************************************************************/

package org.eclipse.birt.report.model.core.namespace;

import java.util.List;

import org.eclipse.birt.report.model.api.elements.ReportDesignConstants;
import org.eclipse.birt.report.model.api.metadata.IElementDefn;
import org.eclipse.birt.report.model.api.metadata.MetaDataConstants;
import org.eclipse.birt.report.model.core.DesignElement;
import org.eclipse.birt.report.model.core.Module;
import org.eclipse.birt.report.model.core.NameSpace;
import org.eclipse.birt.report.model.elements.ReportDesign;
import org.eclipse.birt.report.model.metadata.ElementDefn;
import org.eclipse.birt.report.model.metadata.ElementPropertyDefn;
import org.eclipse.birt.report.model.metadata.MetaDataDictionary;

/**
 * 
 */
class NameExecutorImpl
{

	/**
	 * The core design element that this executor focus on.
	 */
	protected DesignElement focus;

	/**
	 * 
	 */
	public static final String NAME_SEPARATOR = "/"; //$NON-NLS-1$

	/**
	 * 
	 * @param element
	 */
	public NameExecutorImpl( DesignElement element )
	{
		if ( element == null )
			throw new IllegalArgumentException( "The element can not be null" ); //$NON-NLS-1$
		this.focus = element;
	}

	/**
	 * 
	 * @param module
	 * @return the name helper for this executor
	 */
	public final INameHelper getNameHelper( Module module )
	{
		return getNameHelper( module, focus );
	}

	/**
	 * Get name helper of element.
	 * 
	 * @param module
	 * 
	 * @param container
	 * @return <code>ModuleNameHelper</code> or <code>DimensionNameHelper</code>
	 */
	public INameHelper getNameHelper( Module module, DesignElement container )
	{
		DesignElement e = container;
		ElementDefn elementDefn = (ElementDefn) focus.getDefn( );
		IElementDefn holderDefn = elementDefn.getNameConfig( )
				.getNameContainer( );

		if ( holderDefn == null )
			return null;
		IElementDefn moduleDefn = MetaDataDictionary.getInstance( ).getElement(
				ReportDesignConstants.MODULE_ELEMENT );

		// if hold is not module, then search the name container
		if ( !holderDefn.isKindOf( moduleDefn ) )
		{
			if ( holderDefn.getNameOption( ) == MetaDataConstants.NO_NAME )
			{
				if ( module instanceof ReportDesign )
				{
					ElementPropertyDefn targetProperty = (ElementPropertyDefn) elementDefn
							.getNameConfig( ).getNameProperty( );
					if ( targetProperty == null )
						return null;

					Object value = module.getProperty( module, targetProperty );
					if ( value == null )
						return null;

					if ( value instanceof DesignElement )
					{
						assert value instanceof INameContainer;
						return ( (INameContainer) value ).getNameHelper( );
					}
					else if ( value instanceof List )
					{
						List valueList = (List) value;
						if ( valueList.isEmpty( ) )
							return null;
						Object item = valueList.get( 0 );
						assert item instanceof INameContainer;
						return ( (INameContainer) item ).getNameHelper( );
					}

					return null;
				}
				else
				{
					return null;
				}
			}

			while ( e != null )
			{
				if ( e.getDefn( ).isKindOf( holderDefn ) )
				{
					if ( e instanceof INameContainer )
						return ( (INameContainer) e ).getNameHelper( );
				}
				e = e.getContainer( );
			}

			// if not found, then return null
			return null;
		}
		
		if ( module == null )
			return null;
		
		if ( module.getDefn().isKindOf(holderDefn))
			return module.getNameHelper();
		
		return null;
	}

	/**
	 * Gets the name space where the name of this element resides.
	 * 
	 * @param module
	 * @return the namespace instance for this executor
	 */
	public final NameSpace getNameSpace( Module module )
	{
		int id = ( (ElementDefn) focus.getDefn( ) ).getNameSpaceID( );
		INameHelper container = getNameHelper( module );
		return container == null ? null : container.getNameSpace( id );

	}
}


package org.geppetto.core.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.ReplaceCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.geppetto.model.GeppettoLibrary;
import org.geppetto.model.GeppettoModel;
import org.geppetto.model.GeppettoPackage;
import org.geppetto.model.ISynchable;
import org.geppetto.model.Tag;
import org.geppetto.model.datasources.Query;
import org.geppetto.model.types.CompositeType;
import org.geppetto.model.types.ImportType;
import org.geppetto.model.types.SimpleType;
import org.geppetto.model.types.Type;
import org.geppetto.model.types.TypesFactory;
import org.geppetto.model.types.TypesPackage;
import org.geppetto.model.util.GeppettoModelException;
import org.geppetto.model.util.GeppettoVisitingException;
import org.geppetto.model.util.ModelUtility;
import org.geppetto.model.util.PointerUtility;
import org.geppetto.model.values.Pointer;
import org.geppetto.model.variables.Variable;
import org.geppetto.model.variables.VariablesPackage;

/**
 * @author matteocantarelli
 *
 */
public class GeppettoModelAccess
{

	private GeppettoModel geppettoModel;

	private GeppettoLibrary commonlibrary;

	private EditingDomain editingDomain;

	public GeppettoModelAccess(GeppettoModel geppettoModel) throws GeppettoVisitingException
	{
		super();
		this.geppettoModel = geppettoModel;
		editingDomain = AdapterFactoryEditingDomain.getEditingDomainFor(geppettoModel);
		if(editingDomain == null)
		{
			editingDomain = new AdapterFactoryEditingDomain(new ComposedAdapterFactory(), new BasicCommandStack());
		}
		for(GeppettoLibrary library : geppettoModel.getLibraries())
		{
			if(library.getId().equals("common"))
			{
				commonlibrary = library;
				break;
			}
		}
		if(commonlibrary == null)
		{
			throw new GeppettoVisitingException("Common library not found");
		}
	}

	/**
	 * Usage commonLibraryAccess.getType(TypesPackage.Literals.PARAMETER_TYPE);
	 * 
	 * @return
	 * @throws GeppettoVisitingException
	 */
	public Type getType(EClass eclass) throws GeppettoVisitingException
	{
		for(Type type : commonlibrary.getTypes())
		{
			if(type.eClass().equals(eclass))
			{
				return type;
			}
		}
		throw new GeppettoVisitingException("Type for eClass " + eclass + " not found in common library.");
	}
	
	/**
	 * Usage commonLibraryAccess.getType(TypesPackage.Literals.VISUAL_TYPE, "particles");
	 * @param eclass
	 * @param id
	 * @return
	 * @throws GeppettoVisitingException
	 */
	public Type getType(EClass eclass, String id) throws GeppettoVisitingException
	{
		for(Type type : commonlibrary.getTypes())
		{
			if(type.eClass().equals(eclass) && type.getId().equals(id))
			{
				return type;
			}
		}
		throw new GeppettoVisitingException("Type for eClass " + eclass + " not found in common library.");
	}

	/**
	 * @param instancePath
	 * @return
	 * @throws GeppettoModelException
	 */
	public Pointer getPointer(String instancePath) throws GeppettoModelException
	{
		return PointerUtility.getPointer(geppettoModel, instancePath);
	}

	/**
	 * @param variable
	 */
	public void addVariable(final Variable variable)
	{
		Command command = AddCommand.create(editingDomain, geppettoModel, GeppettoPackage.Literals.GEPPETTO_MODEL__VARIABLES, variable);
		editingDomain.getCommandStack().execute(command);
	}

	/**
	 * @param tag
	 */
	public void addTag(final Tag tag)
	{
		Command command = AddCommand.create(editingDomain, geppettoModel, GeppettoPackage.Literals.GEPPETTO_MODEL__TAGS, tag);
		editingDomain.getCommandStack().execute(command);
	}
	
	/**
	 * @param tag
	 */
	public void addLibrary(final GeppettoLibrary library)
	{
		Command command = AddCommand.create(editingDomain, geppettoModel, GeppettoPackage.Literals.GEPPETTO_MODEL__LIBRARIES, library);
		editingDomain.getCommandStack().execute(command);
	}

	/**
	 * @param tag
	 */
	public void addTypeToLibrary(final Type type, final GeppettoLibrary targetLibrary)
	{
		Command command = AddCommand.create(editingDomain, targetLibrary, GeppettoPackage.Literals.GEPPETTO_LIBRARY__TYPES, type);
		editingDomain.getCommandStack().execute(command);
		markAsUnsynched(targetLibrary);
	}

	/**
	 * This method will change an attribute of an object
	 * 
	 * @param object
	 *            the object target of the operation
	 * @param field
	 *            the field to set, needs to come from the Literals enumeration inside the package, e.g. GeppettoPackage.Literals.NODE__NAME to change the name
	 * @param value
	 *            the new value
	 */
	public void setObjectAttribute(final EObject object, Object field, Object value)
	{
		Command setCommand = SetCommand.create(editingDomain, object, field, value);
		editingDomain.getCommandStack().execute(setCommand);
		markAsUnsynched((ISynchable) object);

	}

	/**
	 * This method will set the synched attribute for the object to false indicating that whatever version of the object exists client side it is now out of synch
	 * 
	 * @param object
	 */
	private void markAsUnsynched(ISynchable object)
	{
		Command synchCommand = SetCommand.create(editingDomain, object, GeppettoPackage.Literals.ISYNCHABLE__SYNCHED, false);
		editingDomain.getCommandStack().execute(synchCommand);
	}

	/**
	 * @param typeToRetrieve
	 * @param libraries
	 * @return
	 */
	public Type getOrCreateSimpleType(String typeToRetrieve, List<GeppettoLibrary> libraries)
	{
		for(GeppettoLibrary dependencyLibrary : libraries)
		{
			for(Type candidateSuperType : dependencyLibrary.getTypes())
			{
				if(candidateSuperType.getId().equals(typeToRetrieve))
				{
					return candidateSuperType;
				}
			}
		}
		SimpleType supertypeType = TypesFactory.eINSTANCE.createSimpleType();
		supertypeType.setId(typeToRetrieve);
		supertypeType.setName(typeToRetrieve);
		addTypeToLibrary(supertypeType, libraries.get(0));
		return supertypeType;

	}

	/**
	 * @param newVar
	 * @param targetType
	 */
	public void addVariableToType(Variable newVar, CompositeType targetType)
	{
		Command command = AddCommand.create(editingDomain, targetType, TypesPackage.Literals.COMPOSITE_TYPE__VARIABLES, newVar);
		editingDomain.getCommandStack().execute(command);
		markAsUnsynched(targetType);
	}

	/**
	 * Note this command won't remove the typeToBeReplaced from its container in case it's being iterated over
	 * 
	 * @param typeToBeReplaced
	 * @param newType
	 * @param library
	 */
	public void swapType(ImportType typeToBeReplaced, Type newType, GeppettoLibrary library)
	{
		Command replaceCommand = ReplaceCommand
				.create(editingDomain, typeToBeReplaced.eContainer(), GeppettoPackage.Literals.GEPPETTO_LIBRARY__TYPES, typeToBeReplaced, Collections.singleton(newType));
		editingDomain.getCommandStack().execute(replaceCommand);
		markAsUnsynched((ISynchable) newType.eContainer());

		List<Variable> referencedVars = new ArrayList<Variable>(typeToBeReplaced.getReferencedVariables());
		for(Variable v : referencedVars)
		{
			Command replaceInVarCommand = ReplaceCommand.create(editingDomain, v, VariablesPackage.Literals.VARIABLE__TYPES, typeToBeReplaced, Collections.singleton(newType));
			editingDomain.getCommandStack().execute(replaceInVarCommand);
			markAsUnsynched(v);
		}

	}

	/**
	 * @param object
	 */
	public void removeType(EObject object)
	{
		Command removeCommand = RemoveCommand.create(editingDomain, object.eContainer(), GeppettoPackage.Literals.GEPPETTO_LIBRARY__TYPES, object);
		editingDomain.getCommandStack().execute(removeCommand);

	}

	/**
	 * @param queryPath
	 * @return
	 */
	public Query getQuery(String queryPath)
	{
		return ModelUtility.getQuery(queryPath, geppettoModel);
	}

	/**
	 * @return
	 */
	public List<Query> getQueries()
	{
		return geppettoModel.getQueries();
	}

}

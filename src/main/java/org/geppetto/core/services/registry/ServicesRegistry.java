

package org.geppetto.core.services.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.geppetto.core.conversion.ConversionException;
import org.geppetto.core.conversion.IConversion;
import org.geppetto.core.data.model.ResultsFormat;
import org.geppetto.core.datasources.IQueryProcessor;
import org.geppetto.core.model.IModelInterpreter;
import org.geppetto.core.simulator.ISimulator;
import org.geppetto.model.DomainModel;
import org.geppetto.model.GeppettoFactory;
import org.geppetto.model.ModelFormat;
import org.springframework.aop.TargetClassAware;

/**
 * @author Adrian Quintana (adrian.perez@ucl.ac.uk)
 * 
 */
public class ServicesRegistry
{

	static Set<ModelFormat> registeredModelFormats = new HashSet<ModelFormat>();

	static Map<Class<? extends IModelInterpreter>, List<ModelFormat>> registeredModelInterpreterServices = new HashMap<Class<? extends IModelInterpreter>, List<ModelFormat>>();
	static Map<ConversionServiceKey, List<IConversion>> registeredConversionServices = new HashMap<ConversionServiceKey, List<IConversion>>();
	static Map<Class<? extends ISimulator>, List<ModelFormat>> registeredSimulatorServices = new HashMap<Class<? extends ISimulator>, List<ModelFormat>>();
	static List<Class<? extends IQueryProcessor>> registeredQueryProcessorServices = new ArrayList<Class<? extends IQueryProcessor>>();

	public static ModelFormat registerModelFormat(String format)
	{
		ModelFormat modelFormat = GeppettoFactory.eINSTANCE.createModelFormat();
		modelFormat.setModelFormat(format);
		registeredModelFormats.add(modelFormat);
		return modelFormat;
	}
    
    public static List<String> getRegisteredModelFormats()
    {
        ArrayList<String> modelFormats = new ArrayList<>();
        for(ModelFormat modelFormat : registeredModelFormats)
		{
			modelFormats.add(modelFormat.getModelFormat());
		}
		return modelFormats;
        
    }

	public static ModelFormat getModelFormat(String format)
	{
		for(ModelFormat modelFormat : registeredModelFormats)
		{
			if(modelFormat.getModelFormat().equalsIgnoreCase(format)) return modelFormat;
		}
		return null;
	}

	public static ResultsFormat getResultsFormat(String format)
	{
		for(ResultsFormat resultsFormat : ResultsFormat.values())
		{
			if(resultsFormat.toString().equals(format)) return resultsFormat;
		}
		return null;
	}

	public static void registerModelInterpreterService(IModelInterpreter interpreterService, List<ModelFormat> outputModelFormats)
	{
		registeredModelInterpreterServices.put(interpreterService.getClass(), outputModelFormats);
	}

	public static void registerQueryProcessorService(IQueryProcessor queryProcessor)
	{
		registeredQueryProcessorServices.add(queryProcessor.getClass());

	}

	@SuppressWarnings("rawtypes")
	public static List<ModelFormat> getModelInterpreterServiceFormats(IModelInterpreter interpreterService)
	{
		Class interpreterServiceClass;
		if(interpreterService instanceof TargetClassAware)
		{
			interpreterServiceClass = ((TargetClassAware) interpreterService).getTargetClass();
		}
		else
		{
			interpreterServiceClass = interpreterService.getClass();
		}
		return registeredModelInterpreterServices.get(interpreterServiceClass);
	}

	public static void registerSimulatorService(ISimulator simulatorService, List<ModelFormat> inputModelFormats)
	{
		registeredSimulatorServices.put(simulatorService.getClass(), inputModelFormats);
	}

	@SuppressWarnings("rawtypes")
	public static List<ModelFormat> getSimulatorServiceFormats(ISimulator simulatorService)
	{
		Class simulatorServiceClass;
		if(simulatorService instanceof TargetClassAware)
		{
			simulatorServiceClass = ((TargetClassAware) simulatorService).getTargetClass();
		}
		else
		{
			simulatorServiceClass = simulatorService.getClass();
		}

		return registeredSimulatorServices.get(simulatorServiceClass);
	}

	public static void registerConversionService(IConversion conversionService, List<ModelFormat> inputModelFormats, List<ModelFormat> outputModelFormats)
	{
		for(ModelFormat inputModelFormat : inputModelFormats)
		{
			for(ModelFormat outputModelFormat : outputModelFormats)
			{
				ServicesRegistry.ConversionServiceKey registeredConversionServiceKey = new ConversionServiceKey(inputModelFormat, outputModelFormat);
				List<IConversion> conversionList = registeredConversionServices.get(registeredConversionServiceKey);
				if(conversionList != null)
				{
					conversionList.add(conversionService);
					registeredConversionServices.put(registeredConversionServiceKey, conversionList);
				}
				else
				{
					conversionList = new ArrayList<IConversion>();
					conversionList.add(conversionService);
					registeredConversionServices.put(registeredConversionServiceKey, conversionList);
				}
			}
		}
	}

	public static Map<ModelFormat, List<IConversion>> getSupportedOutputs(DomainModel model)
	{
		Map<ModelFormat, List<IConversion>> supportedOutputs = new HashMap<ModelFormat, List<IConversion>>();
		List<IConversion> checkedConversions = new ArrayList<IConversion>();
		for(Map.Entry<ConversionServiceKey, List<IConversion>> entry : registeredConversionServices.entrySet())
		{
			ConversionServiceKey conversionServiceKey = entry.getKey();
			if(model.getFormat().equals(conversionServiceKey.getInputModelFormat()))
			{
				for(IConversion conversion : entry.getValue())
				{
					try
					{
						if(!checkedConversions.contains(conversion))
						{
							List<ModelFormat> outputs = conversion.getSupportedOutputs(model);
							for(ModelFormat output : outputs)
							{
								List<IConversion> supportedConversions = supportedOutputs.get(output);
								if(supportedConversions != null)
								{
									supportedConversions.add(conversion);
								}
								else
								{
									supportedConversions = new ArrayList<IConversion>();
									supportedConversions.add(conversion);
									supportedOutputs.put(output, supportedConversions);
								}
							}
						}

						checkedConversions.add(conversion);
					}
					catch(ConversionException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}
		return supportedOutputs;
	}

	public static Map<ConversionServiceKey, List<IConversion>> getConversionService(List<ModelFormat> inputModelFormats, List<ModelFormat> outputModelFormats)
	{
		Map<ConversionServiceKey, List<IConversion>> conversionServices = new HashMap<ConversionServiceKey, List<IConversion>>();
		for(ModelFormat inputModelFormat : inputModelFormats)
		{
			for(ModelFormat outputModelFormat : outputModelFormats)
			{
				ServicesRegistry.ConversionServiceKey registeredConversionServiceKey = new ConversionServiceKey(inputModelFormat, outputModelFormat);
				if(registeredConversionServices.containsKey(registeredConversionServiceKey))
				{
					conversionServices.put(registeredConversionServiceKey, registeredConversionServices.get(registeredConversionServiceKey));
				}

			}
		}

		return conversionServices;
	}

	public static List<IConversion> getConversionService(ModelFormat inputModelFormat, ModelFormat outputModelFormat)
	{
		return registeredConversionServices.get(new ConversionServiceKey(inputModelFormat, outputModelFormat));
	}

	public static class ConversionServiceKey
	{

		ModelFormat inputModelFormat;
		ModelFormat outputModelFormat;

		public ConversionServiceKey(ModelFormat inputModelFormat, ModelFormat outputModelFormat)
		{
			super();
			this.inputModelFormat = inputModelFormat;
			this.outputModelFormat = outputModelFormat;
		}

		public ModelFormat getInputModelFormat()
		{
			return inputModelFormat;
		}

		public ModelFormat getOutputModelFormat()
		{
			return outputModelFormat;
		}
        
        @Override
        public String toString()
        {
            return "ConversionServiceKey ["+inputModelFormat.getModelFormat()+"->"+outputModelFormat.getModelFormat()+"; "+hashCode()+"]";
        }

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((inputModelFormat.toString() == null) ? 0 : inputModelFormat.getModelFormat().toUpperCase().hashCode());
			result = prime * result + ((outputModelFormat.toString() == null) ? 0 : outputModelFormat.getModelFormat().toUpperCase().hashCode());
			return result;
		}

		@Override
		public boolean equals(Object o)
		{
			if(o == null || !(o instanceof ConversionServiceKey)) return false;
			ConversionServiceKey other = (ConversionServiceKey) o;
			return o.hashCode()==other.hashCode();
		}

	}

}

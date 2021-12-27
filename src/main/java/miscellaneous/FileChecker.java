package miscellaneous;

import loader.DataLoadCellNovo;
import loader.DataLoadDiasend;
import loader.DataLoadLibreView;
import loader.DataLoadMedtronic;
import loader.DataLoadOmniPod;
import loader.DataLoadRocheCSV;
import loader.DataLoadTandem;

public class FileChecker 
{

	// Opens supplied file and checks validity against known types...
	public enum FileCheckType
	{
		INVALID,

		Medtronic,
		Diasend,
		LibreView,
		OmniPod,
		RocheSQLExtract,
		Tandem,
		CellNovo
	}

	public static String getFileTypeStr(FileCheckType fct)
	{
		String result = new String("");

		switch (fct)
		{
		case INVALID : result = "INVALID"; break;
		case Medtronic : result = "Medtronic"; break;
		case Diasend : result = "Diasend"; break;
		case LibreView : result = "LibreView"; break;
		case OmniPod : result = "OmniPod"; break;
		case RocheSQLExtract : result = "Roche SQL Extract"; break;
		case Tandem : result = "Tandem"; break;
		case CellNovo : result = "CellNovo"; break;

		default : result = "UNKNOWN"; break;
		}

		return result;
	}

	public static FileCheckType checkFile(String filename)
	{
		FileCheckType result = FileChecker.FileCheckType.INVALID;

		// Protect these checks by file name type
		if (filename.contains(".xls"))
		{
			if (isDiasend(filename))
			{
				result = FileChecker.FileCheckType.Diasend;
			}
		}
		else if (filename.contains(".csv"))
		{
			if (isLibreView(filename))
			{
				result = FileChecker.FileCheckType.LibreView;
			}
			else if (isMedtronic(filename))
			{
				result = FileChecker.FileCheckType.Medtronic;
			}
			else if (isRocheCSV(filename))
			{
				result = FileChecker.FileCheckType.RocheSQLExtract;
			}
			else if (isTandem(filename))
			{
				result = FileChecker.FileCheckType.Tandem;
			}
			else if (isCellNovo(filename))
			{
				result = FileChecker.FileCheckType.CellNovo;
			}
		}
		else if (filename.contains(".ibf") && isOmniPod(filename))
		{
			result = FileChecker.FileCheckType.OmniPod;
		}

		return result;
	}

	public static FileCheckType checkBinaryFile(String filename)
	{
		FileCheckType result = FileChecker.FileCheckType.INVALID;

		if (isLibreView(filename))
		{
			result = FileChecker.FileCheckType.LibreView;
		}
		else if (isMedtronic(filename))
		{
			result = FileChecker.FileCheckType.Medtronic;
		}
		else if (isDiasend(filename))
		{
			result = FileChecker.FileCheckType.Diasend;
		}
		else if (isRocheCSV(filename))
		{
			result = FileChecker.FileCheckType.RocheSQLExtract;
		}
		else if (isTandem(filename))
		{
			result = FileChecker.FileCheckType.Tandem;
		}
		else if (isCellNovo(filename))
		{
			result = FileChecker.FileCheckType.CellNovo;
		}

		return result;
	}


	private static boolean isLibreView(String fileName)
	{
		boolean result = DataLoadLibreView.isLibreView(fileName);
		return result;
	}

	private static boolean isMedtronic(String fileName)
	{
		boolean result = DataLoadMedtronic.isMedtronic(fileName);
		return result;
	}

	private static boolean isDiasend(String fileName)
	{
		boolean result = DataLoadDiasend.isDiasend(fileName);
		return result;
	}
	private static boolean isOmniPod(String fileName)
	{
		boolean result = DataLoadOmniPod.isOmniPod(fileName);
		return result;
	}

	private static boolean isRocheCSV(String fileName)
	{
		boolean result = DataLoadRocheCSV.isRoche(fileName);
		return result;
	}

	private static boolean isTandem(String fileName)
	{
		boolean result = DataLoadTandem.isTandem(fileName);
		return result;
	}
	
	private static boolean isCellNovo(String fileName)
	{
		boolean result = DataLoadCellNovo.isCellNovo(fileName);
		return result;
	}
	
}

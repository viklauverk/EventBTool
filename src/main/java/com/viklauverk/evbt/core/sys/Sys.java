/*
 Copyright (C) 2021-2024 Viklauverk AB (agpl-3.0-or-later)

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.viklauverk.evbt.core.sys;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.viklauverk.evbt.core.Pair;
import com.viklauverk.evbt.core.Settings;
import com.viklauverk.evbt.core.ShowSettings;
import com.viklauverk.evbt.core.console.Canvas;
import com.viklauverk.evbt.core.console.Console;
import com.viklauverk.evbt.core.docgen.AllRenders;
import com.viklauverk.evbt.core.docgen.RenderContextHtmq;
import com.viklauverk.evbt.core.docgen.RenderContextSearch;
import com.viklauverk.evbt.core.docgen.RenderContextTeX;
import com.viklauverk.evbt.core.docgen.RenderContextUnicode;
import com.viklauverk.evbt.core.docgen.RenderEventHtmq;
import com.viklauverk.evbt.core.docgen.RenderEventSearch;
import com.viklauverk.evbt.core.docgen.RenderEventTeX;
import com.viklauverk.evbt.core.docgen.RenderEventUnicode;
import com.viklauverk.evbt.core.docgen.RenderFormulaHtmq;
import com.viklauverk.evbt.core.docgen.RenderFormulaSearch;
import com.viklauverk.evbt.core.docgen.RenderFormulaTeX;
import com.viklauverk.evbt.core.docgen.RenderFormulaUnicode;
import com.viklauverk.evbt.core.docgen.RenderMachineHtmq;
import com.viklauverk.evbt.core.docgen.RenderMachineSearch;
import com.viklauverk.evbt.core.docgen.RenderMachineTeX;
import com.viklauverk.evbt.core.docgen.RenderMachineUnicode;
import com.viklauverk.evbt.core.docgen.RenderTarget;
import com.viklauverk.evbt.core.docgen.RenderTheoryHtmq;
import com.viklauverk.evbt.core.docgen.RenderTheorySearch;
import com.viklauverk.evbt.core.docgen.RenderTheoryTeX;
import com.viklauverk.evbt.core.docgen.RenderTheoryUnicode;
import com.viklauverk.evbt.core.edk.EDK;
import com.viklauverk.evbt.core.log.LogModule;

public class Sys
{
    private static LogModule log = LogModule.lookup("sys", Sys.class);

    private String project_info_; // Loaded from project.info and displayed above the table of contents.

    private SymbolTable root_symbol_table_; // Contains TRUE FALSE BOOL
    private Map<String,SymbolTable> all_symbol_tables_;
    private Typing typing_;

    private TheoryPath theory_path_;
    // Used when adding operators for pattern matchin.
    private static Theory dummy_theory_;

    private Map<String,Theory> deployed_theories_ = new HashMap<>();
    private List<Theory> deployed_theory_ordering_ = new ArrayList<>();
    private List<String> deployed_theory_names_ = new ArrayList<>();

    private Map<String,Theory> source_theories_ = new HashMap<>();
    private List<Theory> source_theory_ordering_ = new ArrayList<>();
    private List<String> source_theory_names_ = new ArrayList<>();

    private Map<String,Context> contexts_ = new HashMap<>();
    private List<Context> context_ordering_ = new ArrayList<>();
    private List<String> context_names_ = new ArrayList<>();

    private Map<String,Machine> machines_ = new HashMap<>();
    private List<Machine> machine_ordering_ = new ArrayList<>();
    private List<String> machine_names_ = new ArrayList<>();

    private Map<String,PolymorphicDataType> polymorphic_data_types_  = new HashMap<>();
    private List<PolymorphicDataType> polymorphic_data_type_ordering_ = new ArrayList<>();
    private List<String> polymorphic_data_type_names_ = new ArrayList<>();

    private Map<String,SpecialisedDataType> specialised_data_types_ = new HashMap<>();
    private List<SpecialisedDataType> specialised_data_type_ordering_ = new ArrayList<>();
    private List<String> specialised_data_type_names_ = new ArrayList<>();

    private Settings settings_;
    private Console console_;
    private EDK edk_;

    public Sys(Settings s)
    {
        project_info_ = "";
        theory_path_ = new TheoryPath();

        root_symbol_table_ = new SymbolTable("root");
        all_symbol_tables_ = new HashMap<>();
        all_symbol_tables_.put("root", root_symbol_table_);

        typing_ = new Typing(this);

        CarrierSet BOOL = new CarrierSet("BOOL", null);
        Constant TRUE = new Constant("TRUE", null);
        Constant FALSE = new Constant("FALSE", null);
        BOOL.addMember(TRUE);
        BOOL.addMember(FALSE);
        TRUE.setImplType(typing_.lookupImplType("BOOL"));
        FALSE.setImplType(typing_.lookupImplType("BOOL"));
        root_symbol_table_.addSet(BOOL);
        root_symbol_table_.addConstant(TRUE);
        root_symbol_table_.addConstant(FALSE);

        /*
        CarrierSet evbt_implementations = new CarrierSet("EVBTImplementations", null);
        root_symbol_table_.addSet(evbt_implementations);

        Constant evbt_u8 = new Constant("u8", null);
        Constant evbt_i8 = new Constant("i8", null);
        Constant evbt_arr = new Constant("arr", null);
        evbt_implementations.addMember(evbt_u8);
        evbt_implementations.addMember(evbt_i8);
        evbt_implementations.addMember(evbt_arr);

        root_symbol_table_.addConstant(evbt_u8);
        root_symbol_table_.addConstant(evbt_i8);
        root_symbol_table_.addConstant(evbt_arr);
        */

        settings_ = s;
        console_ = new Console(this, settings_, new Canvas());
        edk_ = new EDK(this);
    }

    public String projectInfo()
    {
        return project_info_;
    }

    public Console console()
    {
        return console_;
    }

    public Settings settings()
    {
        return settings_;
    }

    public EDK edk()
    {
        return edk_;
    }

    public SymbolTable rootSymbolTable()
    {
        return root_symbol_table_;
    }

    public SymbolTable newTheorySymbolTable(String name)
    {
        SymbolTable st = new SymbolTable(name);
        all_symbol_tables_.put(name, st);
        root_symbol_table_.addParent(st);
        return st;
    }

    public SymbolTable newSymbolTable(String name)
    {
        SymbolTable st = new SymbolTable(name);
        st.addParent(root_symbol_table_);
        all_symbol_tables_.put(name, st);
        return st;
    }

    public Map<String,SymbolTable> allSymbolTables()
    {
        return all_symbol_tables_;
    }

    public SymbolTable getSymbolTable(String name)
    {
        return all_symbol_tables_.get(name);
    }

    public Typing typing()
    {
        return typing_;
    }

    public void addDeployedTheory(Theory t)
    {
        deployed_theories_.put(t.name(), t);
        deployed_theory_ordering_.add(t);
        deployed_theory_names_ = deployed_theories_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Theory getDeployedTheory(String name)
    {
        return deployed_theories_.get(name);
    }

    public List<Theory> deployedTheoryOrdering()
    {
        return deployed_theory_ordering_;
    }

    public List<String> deployedTheoryNames()
    {
        return deployed_theory_names_;
    }

    public void addSourceTheory(Theory t)
    {
        source_theories_.put(t.name(), t);
        source_theory_ordering_.add(t);
        source_theory_names_ = source_theories_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Theory getSourceTheory(String name)
    {
        return source_theories_.get(name);
    }

    public List<Theory> sourceTheoryOrdering()
    {
        return source_theory_ordering_;
    }

    public List<String> sourceTheoryNames()
    {
        return source_theory_names_;
    }

    public void addContext(Context c)
    {
        contexts_.put(c.name(), c);
        context_ordering_.add(c);
        context_names_ = contexts_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Context getContext(String name)
    {
        return contexts_.get(name);
    }

    public List<Context> contextOrdering()
    {
        return context_ordering_;
    }

    public List<String> contextNames()
    {
        return context_names_;
    }

    public void addMachine(Machine m)
    {
        machines_.put(m.name(), m);
        machine_ordering_.add(m);
        machine_names_ = machines_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public Machine getMachine(String name)
    {
        return machines_.get(name);
    }

    public List<Machine> machineOrdering()
    {
        return machine_ordering_;
    }

    public List<String> machineNames()
    {
        return machine_names_;
    }

    public String loadTheoriesAndContextsAndMachines(String path, String theory_path) throws Exception
    {
        if (path == null || path.equals("")) return "";

        File dir = new File(path);

        if (!dir.exists() || !dir.isDirectory())
        {
            LogModule.usageErrorStatic("Not a valid directory \"%s\"", path);
        }

        File theory_root_dir = new File(theory_path);

        if (!theory_root_dir.exists() || !theory_root_dir.isDirectory())
        {
            LogModule.usageErrorStatic("Not a valid directory \"%s\"", theory_path);
        }

        // Load list of deployed theories from TheoryPath.
        theory_path_.load(dir, theory_root_dir);
        populateDeployedTheories();

        // First create empty object instances for each theory, context and machine.
        populateContexts(dir);
        populateMachines(dir);

        // Now load and parse the deployed theories. This has to be completely done
        // before parsing contexts and machines, since the DataTypes in the theories
        // are introduced to all contexts and machines within the system through the theory path.
        loadDeployedTheories();
        parseDeployedTheoryFormulas();

        // The root theory symbol tables have now been populated with theory data types and operators!

        // Now load the content, it is now possible to refer to other
        // contexts and machines since we have prepopulated those maps.
        // These will also add the known symbols for sets,constants and variables
        // to the SymbolTables that are needed for parsing the formulas.
        // These will also load the checked types calculated by Rodin.
        loadContexts();
        loadMachines();

        // Now we can actually build the symbol tables and parse the formulas and
        // figure out suitable implementation types and parse the checked types.
        parseContextFormulas();
        parseMachineFormulas();

        if (deployedTheoryNames().size() == 0 && contextNames().size() == 0 && machineNames().size() == 0)
        {
            log.usageError("Nothing to do! No theories, contexts or machines found in %s",  path);
        }

        // Load the projct.info file, if it exists.
        loadProjectInfo(dir);

        return ""+contextNames().size()+" contexts "+machineNames().size()+" machines";
    }

    private List<Pair<String,File>> eachFileEndingIn(File dir, String suffix)
    {
        List<Pair<String,File>> result = new ArrayList<>();

        if (dir.listFiles() == null) return result;

        List<File> files = Stream.of(dir.listFiles()).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

        for (File f : files)
        {
            String name = f.getName();
            int pp = name.lastIndexOf('/');
            if (pp != -1) name = name.substring(pp+1);
            if (name.endsWith(suffix))
            {
                name = name.substring(0, name.length()-suffix.length());
                result.add(new Pair<String,File>(name, f));
            }
        }
        return result;
    }

    private void populateContexts(File dir) throws Exception
    {
        List<Pair<String,File>> files = eachFileEndingIn(dir, ".buc");

        for (Pair<String,File> p : files)
        {
            String name = p.left;
            File file = p.right;
            Context c = new Context(name, this, file);
            addContext(c);
            log.debug("found context "+name);
        }
    }

    /*
    private void populateTheories(File dir, File theory_root_dir) throws Exception
    {
        
        List<Pair<String,File>> files = eachFileEndingIn(dir, ".tuf");

        for (Pair<String,File> p : files)
        {
            String name = p.left;
            File file = p.right;
            Theory t = new Theory(name, this, file, theory_root_dir);
            addTheory(t);
            log.debug("found theory "+name);
            }
    }
*/
    public void populateDeployedTheories() throws Exception
    {
        for (String name : theory_path_.deployedTheories())
        {
            Theory t = getDeployedTheory(name);
            if (t == null)
            {
                addDeployedTheory(new Theory(name, this, theory_path_.getDeployedTheoryDTF(name), null));
                log.debug("populate deployed theory "+name);
            }
        }
    }

    private void populateMachines(File dir) throws Exception
    {
        List<Pair<String,File>> files = eachFileEndingIn(dir, ".bum");

        for (Pair<String,File> p : files)
        {
            String name = p.left;
            File file = p.right;
            Machine m = new Machine(name, this, file);
            addMachine(m);
            log.debug("found machine "+m.name());
        }
    }

    public void loadDeployedTheories() throws Exception
    {
     	for (String name : deployedTheoryNames())
        {
            Theory t = getDeployedTheory(name);
            if (!t.isLoaded())
            {
                t.loadDeployedDTF();
                t.buildSymbolTable();
            }
        }
    }

    private void loadContexts() throws Exception
    {
        for (String name : contextNames())
        {
            Context c = getContext(name);
            c.loadBUC();
            c.loadProofStatus();
            c.loadCheckedTypes();
        }
    }

    private void loadMachines() throws Exception
    {
        for (String name : machineNames())
        {
            Machine m = getMachine(name);
            m.loadBUM();
            m.loadProofStatus();
            m.loadCheckedTypes();
        }
    }

    private void loadProjectInfo(File dir) throws Exception
    {
        List<Pair<String,File>> files = eachFileEndingIn(dir, ".info");

        for (Pair<String,File> p : files)
        {
            if (p.left.equals("project"))
            {
                Stream<String> lines = Files.lines(p.right.toPath());
                project_info_ = lines.collect(Collectors.joining("\n"));
                lines.close();
                log.debug("found project.info");
            }
        }
    }

    private void parseDeployedTheoryFormulas()
    {
        for (String name : deployedTheoryNames())
        {
            Theory t = getDeployedTheory(name);
            t.parse();
        }
    }

    private void parseContextFormulas()
    {
        for (String name : contextNames())
        {
            Context c = getContext(name);
            c.parse();
        }
    }

    private void parseMachineFormulas()
    {
        for (String name : machineNames())
        {
            Machine m = getMachine(name);
            m.parse(rootSymbolTable());
        }
    }

    public void walkSystem(AllRenders ar, String pattern)
    {
        for (Context c : contextOrdering())
        {
            ar.walkContext(c, pattern);
        }

        for (Machine m : machineOrdering())
        {
            ar.walkMachine(m, pattern);
        }

        for (Theory t : deployedTheoryOrdering())
        {
            ar.walkTheory(t, pattern);
        }
    }

    public List<String> listParts(String pattern)
    {
        AllRenders ar = lookupSearchWalker();

        walkSystem(ar, pattern);

        return ar.search().foundParts();
    }

    public String show(ShowSettings ss, Canvas canvas, String pattern)
    {
        AllRenders ar = lookupRenders(RenderTarget.TERMINAL,
                                      canvas);

        if (ss.showingContexts())
        {
            for (Context c : contextOrdering())
            {
                ar.walkContext(c, pattern);
            }
        }

        if (ss.showingMachines())
        {
            for (Machine m : machineOrdering())
            {
                ar.walkMachine(m, pattern);
            }
        }

        return canvas.render();
    }

    AllRenders lookupSearchWalker()
    {
        return new AllRenders(
            new RenderContextSearch(),
            new RenderMachineSearch(),
            new RenderEventSearch(),
            new RenderFormulaSearch(null),
            new RenderTheorySearch(),
            null);
    }

    public AllRenders lookupRenders(RenderTarget format, Canvas canvas)
    {
        switch (format)
        {
        case PLAIN:
            return new AllRenders(new RenderContextUnicode(),
                                  new RenderMachineUnicode(),
                                  new RenderEventUnicode(),
                                  new RenderFormulaUnicode(canvas),
                                  new RenderTheoryUnicode(),
                                  canvas);
        case TERMINAL:
            return new AllRenders(new RenderContextUnicode(),
                                  new RenderMachineUnicode(),
                                  new RenderEventUnicode(),
                                  new RenderFormulaUnicode(canvas),
                                  new RenderTheoryUnicode(),
                                  canvas);
        case TEX:
           return new AllRenders(new RenderContextTeX(),
                                 new RenderMachineTeX(),
                                 new RenderEventTeX(),
                                 new RenderFormulaTeX(canvas),
                                 new RenderTheoryTeX(),
                                 canvas);
        case HTML:
           return new AllRenders(new RenderContextHtmq(),
                                 new RenderMachineHtmq(),
                                 new RenderEventHtmq(),
                                 new RenderFormulaHtmq(canvas),
                                 new RenderTheoryHtmq(),
                                 canvas);
        }
        assert (false) : "No case for format: "+format;
        return null;
    }

    public static synchronized Theory dummyTheory()
    {
        if (dummy_theory_ == null)
        {
            dummy_theory_ = new Theory("Dummy", null, null, null);
        }

        return dummy_theory_;
    }

    public TheoryPath theoryPath()
    {
        return theory_path_;
    }

    public void addSpecialisedDataType(SpecialisedDataType sdt)
    {
        specialised_data_types_.put(sdt.longName(), sdt);
        specialised_data_type_ordering_.add(sdt);
        specialised_data_type_names_ = specialised_data_types_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public SpecialisedDataType getSpecialisedDataType(String name)
    {
        return specialised_data_types_.get(name);
    }

    public List<SpecialisedDataType> specialisedDataTypeOrdering()
    {
        return specialised_data_type_ordering_;
    }

    public List<String> specialisedDataTypeNames()
    {
        return specialised_data_type_names_;
    }

    public void addPolymorphicDataType(PolymorphicDataType pdt)
    {
        polymorphic_data_types_.put(pdt.baseName(), pdt);
        polymorphic_data_type_ordering_.add(pdt);
        polymorphic_data_type_names_ = polymorphic_data_types_.keySet().stream().sorted().collect(Collectors.toList());
    }

    public PolymorphicDataType getPolymorphicDataType(String name)
    {
        return polymorphic_data_types_.get(name);
    }

    public List<PolymorphicDataType> polymorphicDataTypeOrdering()
    {
        return polymorphic_data_type_ordering_;
    }

    public List<String> polymorphicDataTypeNames()
    {
        return polymorphic_data_type_names_;
    }
}

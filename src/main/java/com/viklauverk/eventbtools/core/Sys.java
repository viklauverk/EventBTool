/*
 Copyright (C) 2021-2024 Viklauverk AB

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

package com.viklauverk.eventbtools.core;

import java.net.URL;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Comparator;

public class Sys
{
    private static LogModule log = LogModule.lookup("sys");

    private String project_info_; // Loaded from project.info and displayed above the table of contents.

    private SymbolTable root_symbol_table_; // Contains TRUE FALSE BOOL
    private Map<String,SymbolTable> all_symbol_tables_;
    private Typing typing_;

    private TheoryPath theory_path_;
    // Used when adding operators for pattern matchin.
    private static Theory dummy_theory_;

    private Map<String,Theory> deployed_theories_;
    private List<Theory> deployed_theory_ordering_;
    private List<String> deployed_theory_names_;

    private Map<String,Theory> source_theories_;
    private List<Theory> source_theory_ordering_;
    private List<String> source_theory_names_;

    private Map<String,Context> contexts_;
    private List<Context> context_ordering_;
    private List<String> context_names_;

    private Map<String,Machine> machines_;
    private List<Machine> machine_ordering_;
    private List<String> machine_names_;

    private Settings settings_;
    private Console console_;
    private EDK edk_;

    @SuppressWarnings("this-escape")
    public Sys(Settings s)
    {
        project_info_ = "";
        theory_path_ = new TheoryPath();

        deployed_theories_ = new HashMap<>();
        deployed_theory_ordering_ = new ArrayList<>();
        deployed_theory_names_ = new ArrayList<>();
        source_theories_ = new HashMap<>();
        source_theory_ordering_ = new ArrayList<>();
        source_theory_names_ = new ArrayList<>();
        contexts_ = new HashMap<>();
        context_ordering_ = new ArrayList<>();
        context_names_ = new ArrayList<>();
        machines_ = new HashMap<>();
        machine_ordering_ = new ArrayList<>();
        machine_names_ = new ArrayList<>();

        root_symbol_table_ = new SymbolTable("root");
        all_symbol_tables_ = new HashMap<>();
        all_symbol_tables_.put("root", root_symbol_table_);

        typing_ = new Typing();

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

    private void populateTheories(File dir, File theory_root_dir) throws Exception
    {
        /*
        List<Pair<String,File>> files = eachFileEndingIn(dir, ".tuf");

        for (Pair<String,File> p : files)
        {
            String name = p.left;
            File file = p.right;
            Theory t = new Theory(name, this, file, theory_root_dir);
            addTheory(t);
            log.debug("found theory "+name);
            }*/
    }

    public void populateDeployedTheories() throws Exception
    {
        for (String name : theory_path_.deployedTheories())
        {
            addDeployedTheory(new Theory(name, this, theory_path_.getDeployedTheoryDTF(name), null));
            log.debug("found deployed theory "+name);
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

    private void loadDeployedTheories() throws Exception
    {
     	for (String name : deployedTheoryNames())
        {
            Theory t = getDeployedTheory(name);
            t.loadDeployedDTF();
            t.buildSymbolTable();
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
            new RenderPolymorphicDataTypeSearch(),
            null);
    }

    AllRenders lookupRenders(RenderTarget format, Canvas canvas)
    {
        switch (format)
        {
        case PLAIN:
            return new AllRenders(new RenderContextUnicode(),
                                  new RenderMachineUnicode(),
                                  new RenderEventUnicode(),
                                  new RenderFormulaUnicode(canvas),
                                  new RenderTheoryUnicode(),
                                  new RenderPolymorphicDataTypeUnicode(),
                                  canvas);
        case TERMINAL:
            return new AllRenders(new RenderContextUnicode(),
                                  new RenderMachineUnicode(),
                                  new RenderEventUnicode(),
                                  new RenderFormulaUnicode(canvas),
                                  new RenderTheoryUnicode(),
                                  new RenderPolymorphicDataTypeUnicode(),
                                  canvas);
        case TEX:
           return new AllRenders(new RenderContextTeX(),
                                 new RenderMachineTeX(),
                                 new RenderEventTeX(),
                                 new RenderFormulaTeX(canvas),
                                 new RenderTheoryTeX(),
                                 new RenderPolymorphicDataTypeTeX(),
                                 canvas);
        case HTML:
           return new AllRenders(new RenderContextHtmq(),
                                 new RenderMachineHtmq(),
                                 new RenderEventHtmq(),
                                 new RenderFormulaHtmq(canvas),
                                 new RenderTheoryHtmq(),
                                 new RenderPolymorphicDataTypeHtmq(),
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
}

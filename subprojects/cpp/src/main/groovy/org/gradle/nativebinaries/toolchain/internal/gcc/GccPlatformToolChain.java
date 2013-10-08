/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gradle.nativebinaries.toolchain.internal.gcc;

import org.gradle.api.internal.tasks.compile.Compiler;
import org.gradle.nativebinaries.internal.BinaryToolSpec;
import org.gradle.nativebinaries.internal.LinkerSpec;
import org.gradle.nativebinaries.internal.PlatformToolChain;
import org.gradle.nativebinaries.internal.StaticLibraryArchiverSpec;
import org.gradle.nativebinaries.language.assembler.internal.AssembleSpec;
import org.gradle.nativebinaries.language.c.internal.CCompileSpec;
import org.gradle.nativebinaries.language.cpp.internal.CppCompileSpec;
import org.gradle.nativebinaries.toolchain.internal.CommandLineTool;
import org.gradle.nativebinaries.toolchain.internal.ToolType;
import org.gradle.process.internal.ExecActionFactory;

class GccPlatformToolChain implements PlatformToolChain {
    private final ToolRegistry tools;
    private final ExecActionFactory execActionFactory;
    private final boolean useCommandFile;

    GccPlatformToolChain(ToolRegistry tools, ExecActionFactory execActionFactory, boolean useCommandFile) {
        this.execActionFactory = execActionFactory;
        this.tools = tools;
        this.useCommandFile = useCommandFile;
    }

    public <T extends BinaryToolSpec> org.gradle.api.internal.tasks.compile.Compiler<T> createCppCompiler() {
        CommandLineTool<CppCompileSpec> commandLineTool = commandLineTool(ToolType.CPP_COMPILER);
        return (org.gradle.api.internal.tasks.compile.Compiler<T>) new CppCompiler(commandLineTool, useCommandFile);
    }

    public <T extends BinaryToolSpec> org.gradle.api.internal.tasks.compile.Compiler<T> createCCompiler() {
        CommandLineTool<CCompileSpec> commandLineTool = commandLineTool(ToolType.C_COMPILER);
        return (Compiler<T>) new CCompiler(commandLineTool, useCommandFile);
    }

    public <T extends BinaryToolSpec> Compiler<T> createAssembler() {
        CommandLineTool<AssembleSpec> commandLineTool = commandLineTool(ToolType.ASSEMBLER);
        return (Compiler<T>) new Assembler(commandLineTool);
    }

    public <T extends LinkerSpec> Compiler<T> createLinker() {
        CommandLineTool<LinkerSpec> commandLineTool = commandLineTool(ToolType.LINKER);
        return (Compiler<T>) new GccLinker(commandLineTool, useCommandFile);
    }

    public <T extends StaticLibraryArchiverSpec> Compiler<T> createStaticLibraryArchiver() {
        CommandLineTool<StaticLibraryArchiverSpec> commandLineTool = commandLineTool(ToolType.STATIC_LIB_ARCHIVER);
        return (Compiler<T>) new ArStaticLibraryArchiver(commandLineTool);
    }

    private <T extends BinaryToolSpec> CommandLineTool<T> commandLineTool(ToolType key) {
        CommandLineTool<T> commandLineTool = new CommandLineTool<T>(key.getToolName(), tools.locate(key), execActionFactory);
        // MinGW requires the path to be set
        commandLineTool.withPath(tools.getPath());
        return commandLineTool;
    }
}

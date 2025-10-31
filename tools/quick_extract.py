#!/usr/bin/env python3
"""
Quick Extract Tool
Quickly extract and view specific files from manufacturer's zip
"""

import os
import sys
import zipfile
import tempfile
import shutil
from pathlib import Path

class QuickExtract:
    def __init__(self, zip_path):
        self.zip_path = Path(zip_path)
        self.temp_dir = None
        self.extracted_path = None
        
    def extract_zip(self):
        """Extract zip file"""
        if self.temp_dir:
            return True
            
        self.temp_dir = tempfile.mkdtemp(prefix="quick_extract_")
        
        try:
            with zipfile.ZipFile(self.zip_path, 'r') as zip_ref:
                zip_ref.extractall(self.temp_dir)
            
            extracted_items = list(Path(self.temp_dir).iterdir())
            if len(extracted_items) == 1 and extracted_items[0].is_dir():
                self.extracted_path = extracted_items[0]
            else:
                self.extracted_path = Path(self.temp_dir)
                
            return True
            
        except Exception as e:
            print(f"❌ Error: {e}")
            return False
    
    def list_files(self, pattern=None):
        """List all files in the zip"""
        if not self.extract_zip():
            return
        
        print("📁 FILES IN MANUFACTURER'S ZIP:")
        print("=" * 60)
        
        files = []
        for file_path in self.extracted_path.rglob('*'):
            if file_path.is_file():
                rel_path = str(file_path.relative_to(self.extracted_path))
                if pattern is None or pattern.lower() in rel_path.lower():
                    files.append(rel_path)
        
        files.sort()
        for i, file_path in enumerate(files, 1):
            print(f"{i:3d}. {file_path}")
        
        return files
    
    def show_file(self, file_path):
        """Show content of a specific file"""
        if not self.extract_zip():
            return
        
        full_path = self.extracted_path / file_path
        if not full_path.exists():
            print(f"❌ File not found: {file_path}")
            return
        
        try:
            with open(full_path, 'r', encoding='utf-8') as f:
                content = f.read()
        except UnicodeDecodeError:
            try:
                with open(full_path, 'r', encoding='latin-1') as f:
                    content = f.read()
            except:
                print(f"❌ Cannot read file (binary?): {file_path}")
                return
        
        print(f"\n📄 FILE: {file_path}")
        print("=" * 80)
        print(f"Size: {len(content)} characters, {len(content.splitlines())} lines")
        print("=" * 80)
        
        lines = content.splitlines()
        for i, line in enumerate(lines, 1):
            print(f"{i:4d}: {line}")
    
    def extract_file(self, file_path, output_name=None):
        """Extract a specific file to current directory"""
        if not self.extract_zip():
            return
        
        source_path = self.extracted_path / file_path
        if not source_path.exists():
            print(f"❌ File not found: {file_path}")
            return
        
        if output_name is None:
            output_name = f"manufacturer_{file_path.replace('/', '_')}"
        
        try:
            shutil.copy2(source_path, output_name)
            print(f"✅ Extracted to: {output_name}")
        except Exception as e:
            print(f"❌ Error extracting: {e}")
    
    def cleanup(self):
        """Clean up"""
        if self.temp_dir and os.path.exists(self.temp_dir):
            shutil.rmtree(self.temp_dir)

def main():
    if len(sys.argv) < 2:
        print("Usage:")
        print("  python3 quick_extract.py list [pattern]")
        print("  python3 quick_extract.py show <file_path>")
        print("  python3 quick_extract.py extract <file_path> [output_name]")
        return 1
    
    zip_path = "/home/revdev/Desktop/Daily/Devalaya/PowerBank/Emqx/Device-related tools/device-util-demo.zip"
    
    if not os.path.exists(zip_path):
        print(f"❌ Zip file not found: {zip_path}")
        return 1
    
    extractor = QuickExtract(zip_path)
    
    try:
        command = sys.argv[1]
        
        if command == 'list':
            pattern = sys.argv[2] if len(sys.argv) > 2 else None
            extractor.list_files(pattern)
            
        elif command == 'show':
            if len(sys.argv) < 3:
                print("❌ Please specify file path")
                return 1
            extractor.show_file(sys.argv[2])
            
        elif command == 'extract':
            if len(sys.argv) < 3:
                print("❌ Please specify file path")
                return 1
            output_name = sys.argv[3] if len(sys.argv) > 3 else None
            extractor.extract_file(sys.argv[2], output_name)
            
        else:
            print(f"❌ Unknown command: {command}")
            return 1
            
    finally:
        extractor.cleanup()
    
    return 0

if __name__ == "__main__":
    sys.exit(main())
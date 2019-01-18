# Exercise Maker Introduction
a maven plugin that *produces the corresponding exercise files based on the example files* in a training project for training attendees. So that when we modify an example file slightly in the future, we don't have to create the corresponding exercise file again manually.

# Requirements
1. We can put a exercise marker in the files of a training project to instruct exercise maker to remove lines in order to produce exercise files.
2. All files of a training project could be turned into exercise files including java, zul, css, or javascript.
3. if a file contains no exercise marker, then it doesn't generate a exercise file.
Since it's a waste to generate an identical file.

# Feature
* The plugin removes the specified range of lines below TODO mark and produces the corresponding file in the output directory for the exercise, e.g.
`<!-- TODO, 2-5, hint -->`
The plugin removes the lines starting from the 2nd to the 5th lines (included) below TODO.
* If a file doesn't contain any exercise mark, do not produce any exercise file.

# Syntax
a valid mark should at least contains 3 parts:
`TODO, [RANGE OF LINE], [HINT]`
* The leading **TODO** must be uppercase

## `[RANGE OF LINE]`:
1. line number starts from *1* 
2. one number, number of lines to delete e.g. `13`
3. a range e.g. `1-3`.
4. ignore invalid mark

## `[HINT]` 
* a hint can have a comma, the plugin just check the first 2 parts
* the plugin should ignore those markers with invalid syntax


# Usage
Mark a block to be removed for exercises
## zul
`<!-- TODO, [RANGE OF LINE], [IGNORED, exercise hint] -->`

## java
`// TODO, [RANGE OF LINE], [IGNORED, exercise hint]`

 
`[IGNORED]`
1. the plugin ignores this segment and doesn't process it
2. we can write a hint for readers for the answer.
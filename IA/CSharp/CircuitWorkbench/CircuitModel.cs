using System;
using System.Collections.Generic;

namespace CircuitWorkbench
{
    public enum ComponentKind
    {
        High,
        Input,
        Switch,
        Clock,
        And,
        Or,
        Not,
        Junction,
        Output,
        Led,
        Display8,
        Keypad8,
        FlipFlopD,
        FlipFlopJK
    }

    public class CircuitComponent
    {
        public int Id { get; set; }

        public ComponentKind Kind { get; set; }

        public double X { get; set; }

        public double Y { get; set; }

        public bool ToggleValue { get; set; }

        public bool ClockValue { get; set; }

        public byte NumericValue { get; set; }

        public bool StoredValue { get; set; }

        public bool PreviousClock { get; set; }

        public bool[] OutputStates { get; set; }

        public CircuitComponent()
        {
            OutputStates = new bool[0];
        }

        public int InputCount
        {
            get
            {
                switch (Kind)
                {
                    case ComponentKind.And:
                    case ComponentKind.Or:
                    case ComponentKind.FlipFlopD:
                        return 2;

                    case ComponentKind.FlipFlopJK:
                        return 3;

                    case ComponentKind.Display8:
                        return 8;

                    case ComponentKind.Not:
                    case ComponentKind.Junction:
                    case ComponentKind.Output:
                    case ComponentKind.Led:
                        return 1;

                    default:
                        return 0;
                }
            }
        }

        public int OutputCount
        {
            get
            {
                switch (Kind)
                {
                    case ComponentKind.Output:
                    case ComponentKind.Led:
                    case ComponentKind.Display8:
                        return 0;

                    case ComponentKind.Keypad8:
                        return 8;

                    default:
                        return 1;
                }
            }
        }

        public double Width
        {
            get
            {
                if (Kind == ComponentKind.Display8 ||
                    Kind == ComponentKind.Keypad8)
                {
                    return 225;
                }

                return 175;
            }
        }

        public double Height
        {
            get
            {
                int count = Math.Max(
                    InputCount,
                    OutputCount
                );

                return Math.Max(
                    110,
                    65 + count * 23
                );
            }
        }

        public string Title
        {
            get
            {
                switch (Kind)
                {
                    case ComponentKind.High:
                        return "NÍVEL ALTO";

                    case ComponentKind.Input:
                        return "ENTRADA";

                    case ComponentKind.Switch:
                        return "INTERRUPTOR";

                    case ComponentKind.Clock:
                        return "CLOCK";

                    case ComponentKind.And:
                        return "AND";

                    case ComponentKind.Or:
                        return "OR";

                    case ComponentKind.Not:
                        return "NOT";

                    case ComponentKind.Junction:
                        return "JUNÇÃO";

                    case ComponentKind.Output:
                        return "SAÍDA";

                    case ComponentKind.Led:
                        return "LED";

                    case ComponentKind.Display8:
                        return "VISOR 8 BITS";

                    case ComponentKind.Keypad8:
                        return "TECLADO NUMÉRICO";

                    case ComponentKind.FlipFlopD:
                        return "FLIP-FLOP D";

                    case ComponentKind.FlipFlopJK:
                        return "FLIP-FLOP JK";

                    default:
                        return "COMPONENTE";
                }
            }
        }

        public string InputName(int index)
        {
            if (index < 0 || index >= InputCount)
            {
                throw new ArgumentOutOfRangeException("index");
            }

            switch (Kind)
            {
                case ComponentKind.And:
                case ComponentKind.Or:
                    return index == 0 ? "A" : "B";

                case ComponentKind.FlipFlopD:
                    return index == 0 ? "D" : "CLK";

                case ComponentKind.FlipFlopJK:
                    if (index == 0)
                    {
                        return "J";
                    }

                    return index == 1 ? "K" : "CLK";

                case ComponentKind.Display8:
                    return "b" + index;

                default:
                    return "IN";
            }
        }

        public string OutputName(int index)
        {
            if (index < 0 || index >= OutputCount)
            {
                throw new ArgumentOutOfRangeException("index");
            }

            if (Kind == ComponentKind.Keypad8)
            {
                return "b" + index;
            }

            if (Kind == ComponentKind.Clock)
            {
                return "CLK";
            }

            return "Q";
        }

        public bool IsManualSource
        {
            get
            {
                return Kind == ComponentKind.Input ||
                       Kind == ComponentKind.Switch;
            }
        }
    }

    public class CircuitWire
    {
        public CircuitComponent Source { get; set; }

        public int SourcePin { get; set; }

        public CircuitComponent Target { get; set; }

        public int TargetPin { get; set; }
    }

    public class CircuitModel
    {
        private readonly List<CircuitComponent> components =
            new List<CircuitComponent>();

        private readonly List<CircuitWire> wires =
            new List<CircuitWire>();

        private int nextId = 1;

        public bool LastPropagationConverged { get; private set; }

        public IList<CircuitComponent> Components
        {
            get
            {
                return components.AsReadOnly();
            }
        }

        public IList<CircuitWire> Wires
        {
            get
            {
                return wires.AsReadOnly();
            }
        }

        public CircuitModel()
        {
            LastPropagationConverged = true;
        }

        public CircuitComponent Add(
            ComponentKind kind,
            double x,
            double y
        )
        {
            CircuitComponent component =
                new CircuitComponent();

            component.Id = nextId++;
            component.Kind = kind;
            component.X = x;
            component.Y = y;

            component.OutputStates = new bool[
                component.OutputCount
            ];

            components.Add(component);

            Evaluate();

            return component;
        }

        public void Remove(CircuitComponent component)
        {
            if (!components.Contains(component))
            {
                return;
            }

            for (int i = wires.Count - 1; i >= 0; i--)
            {
                if (wires[i].Source == component ||
                    wires[i].Target == component)
                {
                    wires.RemoveAt(i);
                }
            }

            components.Remove(component);

            Evaluate();
        }

        public void RemoveWire(CircuitWire wire)
        {
            wires.Remove(wire);

            Evaluate();
        }

        public void Connect(
            CircuitComponent source,
            int sourcePin,
            CircuitComponent target,
            int targetPin
        )
        {
            if (!components.Contains(source) ||
                !components.Contains(target))
            {
                throw new InvalidOperationException(
                    "Um dos componentes não pertence ao circuito."
                );
            }

            if (source == target)
            {
                throw new InvalidOperationException(
                    "Não é permitido conectar diretamente " +
                    "um componente a si próprio."
                );
            }

            if (sourcePin < 0 ||
                sourcePin >= source.OutputCount)
            {
                throw new InvalidOperationException(
                    "O terminal de saída selecionado é inválido."
                );
            }

            if (targetPin < 0 ||
                targetPin >= target.InputCount)
            {
                throw new InvalidOperationException(
                    "O terminal de entrada selecionado é inválido."
                );
            }

            foreach (CircuitWire existing in wires)
            {
                if (existing.Target == target &&
                    existing.TargetPin == targetPin)
                {
                    throw new InvalidOperationException(
                        "Esta entrada já possui uma ligação. " +
                        "Remova o fio atual antes de conectar outro."
                    );
                }
            }

            CircuitWire wire = new CircuitWire();

            wire.Source = source;
            wire.SourcePin = sourcePin;
            wire.Target = target;
            wire.TargetPin = targetPin;

            wires.Add(wire);

            Evaluate();
        }

        public bool ReadInput(
            CircuitComponent component,
            int inputPin
        )
        {
            if (inputPin < 0 ||
                inputPin >= component.InputCount)
            {
                throw new ArgumentOutOfRangeException(
                    "inputPin"
                );
            }

            foreach (CircuitWire wire in wires)
            {
                if (wire.Target == component &&
                    wire.TargetPin == inputPin)
                {
                    return wire.Source.OutputStates[
                        wire.SourcePin
                    ];
                }
            }

            return false;
        }

        public bool IsInputConnected(
            CircuitComponent component,
            int inputPin
        )
        {
            foreach (CircuitWire wire in wires)
            {
                if (wire.Target == component &&
                    wire.TargetPin == inputPin)
                {
                    return true;
                }
            }

            return false;
        }

        public bool Evaluate()
        {
            int maximumIterations = Math.Max(
                16,
                components.Count * 4
            );

            for (int iteration = 0;
                 iteration < maximumIterations;
                 iteration++)
            {
                bool changed = false;

                foreach (CircuitComponent component in components)
                {
                    for (int pin = 0;
                         pin < component.OutputCount;
                         pin++)
                    {
                        bool value = ComputeOutput(
                            component,
                            pin
                        );

                        if (component.OutputStates[pin] != value)
                        {
                            component.OutputStates[pin] = value;

                            changed = true;
                        }
                    }
                }

                if (!changed)
                {
                    LastPropagationConverged = true;
                    return true;
                }
            }

            LastPropagationConverged = false;
            return false;
        }

        private bool ComputeOutput(
            CircuitComponent component,
            int outputPin
        )
        {
            switch (component.Kind)
            {
                case ComponentKind.High:
                    return true;

                case ComponentKind.Input:
                case ComponentKind.Switch:
                    return component.ToggleValue;

                case ComponentKind.Clock:
                    return component.ClockValue;

                case ComponentKind.And:
                    return ReadInput(component, 0) &&
                           ReadInput(component, 1);

                case ComponentKind.Or:
                    return ReadInput(component, 0) ||
                           ReadInput(component, 1);

                case ComponentKind.Not:
                    return !ReadInput(component, 0);

                case ComponentKind.Junction:
                    return ReadInput(component, 0);

                case ComponentKind.Keypad8:
                    return
                        (component.NumericValue &
                        (1 << outputPin)) != 0;

                case ComponentKind.FlipFlopD:
                case ComponentKind.FlipFlopJK:
                    return component.StoredValue;

                default:
                    return false;
            }
        }

        public void Toggle(CircuitComponent component)
        {
            if (!components.Contains(component))
            {
                throw new InvalidOperationException(
                    "Componente não encontrado."
                );
            }

            if (!component.IsManualSource)
            {
                throw new InvalidOperationException(
                    "Este componente não permite alternância manual."
                );
            }

            component.ToggleValue = !component.ToggleValue;

            Evaluate();
        }

        public void SetNumericValue(
            CircuitComponent component,
            byte value
        )
        {
            if (!components.Contains(component) ||
                component.Kind != ComponentKind.Keypad8)
            {
                throw new InvalidOperationException(
                    "Selecione um teclado numérico."
                );
            }

            component.NumericValue = value;

            Evaluate();
        }

        public void AdvanceStep()
        {
            foreach (CircuitComponent component in components)
            {
                if (component.Kind == ComponentKind.Clock)
                {
                    component.ClockValue = !component.ClockValue;
                }
            }

            bool initialConvergence = Evaluate();

            Dictionary<CircuitComponent, bool> nextStates =
                new Dictionary<CircuitComponent, bool>();

            foreach (CircuitComponent component in components)
            {
                if (component.Kind == ComponentKind.FlipFlopD)
                {
                    bool data = ReadInput(component, 0);
                    bool clock = ReadInput(component, 1);

                    bool risingEdge =
                        !component.PreviousClock && clock;

                    bool nextValue = component.StoredValue;

                    if (risingEdge)
                    {
                        nextValue = data;
                    }

                    nextStates.Add(component, nextValue);

                    component.PreviousClock = clock;
                }
                else if (
                    component.Kind == ComponentKind.FlipFlopJK
                )
                {
                    bool j = ReadInput(component, 0);
                    bool k = ReadInput(component, 1);
                    bool clock = ReadInput(component, 2);

                    bool risingEdge =
                        !component.PreviousClock && clock;

                    bool nextValue = component.StoredValue;

                    if (risingEdge)
                    {
                        if (!j && !k)
                        {
                            nextValue = component.StoredValue;
                        }
                        else if (!j && k)
                        {
                            nextValue = false;
                        }
                        else if (j && !k)
                        {
                            nextValue = true;
                        }
                        else
                        {
                            nextValue = !component.StoredValue;
                        }
                    }

                    nextStates.Add(component, nextValue);

                    component.PreviousClock = clock;
                }
            }

            foreach (
                KeyValuePair<CircuitComponent, bool> state
                in nextStates
            )
            {
                state.Key.StoredValue = state.Value;
            }

            bool finalConvergence = Evaluate();

            LastPropagationConverged =
                initialConvergence && finalConvergence;
        }

        public byte ReadDisplay(
            CircuitComponent component
        )
        {
            if (component.Kind != ComponentKind.Display8)
            {
                throw new InvalidOperationException(
                    "O componente não é um visor de oito bits."
                );
            }

            int value = 0;

            for (int bit = 0; bit < 8; bit++)
            {
                if (ReadInput(component, bit))
                {
                    value |= 1 << bit;
                }
            }

            return (byte)value;
        }

        public bool ReadIndicator(
            CircuitComponent component
        )
        {
            if (component.Kind != ComponentKind.Led &&
                component.Kind != ComponentKind.Output)
            {
                throw new InvalidOperationException(
                    "O componente não é um indicador lógico."
                );
            }

            return ReadInput(component, 0);
        }
    }
}
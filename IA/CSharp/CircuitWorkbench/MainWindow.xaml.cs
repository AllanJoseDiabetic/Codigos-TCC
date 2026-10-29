using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Shapes;
using System.Windows.Threading;

namespace CircuitWorkbench
{
    public partial class MainWindow : Window
    {
        private class ComponentVisual
        {
            public CircuitComponent Component;
            public Border Body;
            public TextBlock Title;
            public TextBlock Detail;
            public Ellipse LedIndicator;

            public List<Ellipse> InputPins =
                new List<Ellipse>();

            public List<Ellipse> OutputPins =
                new List<Ellipse>();

            public List<TextBlock> InputLabels =
                new List<TextBlock>();

            public List<TextBlock> OutputLabels =
                new List<TextBlock>();
        }

        private readonly CircuitModel circuit =
            new CircuitModel();

        private readonly Dictionary<CircuitComponent, ComponentVisual>
            visuals =
                new Dictionary<CircuitComponent, ComponentVisual>();

        private readonly DispatcherTimer simulationTimer =
            new DispatcherTimer();

        private CircuitComponent selectedComponent;
        private CircuitComponent draggingComponent;
        private CircuitComponent pendingSource;

        private int pendingSourcePin;
        private int placementIndex;

        private Point dragStart;
        private double dragOriginalX;
        private double dragOriginalY;

        private bool running;
        private bool paused;

        private string numericDraft = "";

        private const double PinTop = 45;
        private const double PinSpacing = 23;

        private static readonly Brush HighSignalBrush =
            new SolidColorBrush(
                Color.FromRgb(74, 222, 128)
            );

        private static readonly Brush LowSignalBrush =
            new SolidColorBrush(
                Color.FromRgb(107, 114, 128)
            );

        private static readonly Brush ComponentBrush =
            new SolidColorBrush(
                Color.FromRgb(35, 47, 65)
            );

        private static readonly Brush SelectedBrush =
            new SolidColorBrush(
                Color.FromRgb(59, 130, 246)
            );

        private static readonly Brush ComponentBorderBrush =
            new SolidColorBrush(
                Color.FromRgb(100, 116, 139)
            );

        public MainWindow()
        {
            InitializeComponent();

            simulationTimer.Interval =
                TimeSpan.FromMilliseconds(100);

            simulationTimer.Tick += SimulationTimer_Tick;

            IntervalSlider.Value = 100;

            IntervalText.Text = "100 ms";

            UpdateSimulationState();

            RefreshAll();
        }

        private void Palette_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            Button button = sender as Button;

            if (button == null || button.Tag == null)
            {
                return;
            }

            ComponentKind kind = (ComponentKind)Enum.Parse(
                typeof(ComponentKind),
                button.Tag.ToString()
            );

            int index = placementIndex % 24;

            double x = 40 + (index % 6) * 290;
            double y = 40 + (index / 6) * 290;

            placementIndex++;

            CircuitComponent component = circuit.Add(
                kind,
                x,
                y
            );

            CreateComponentVisual(component);

            SelectComponent(component);

            RefreshAll();

            SetStatus(
                "Componente inserido: " +
                component.Title +
                " #" + component.Id + "."
            );
        }

        private void CreateComponentVisual(
            CircuitComponent component
        )
        {
            ComponentVisual view = new ComponentVisual();

            view.Component = component;

            Border body = new Border();

            body.Width = component.Width;
            body.Height = component.Height;

            body.Background = ComponentBrush;
            body.BorderBrush = ComponentBorderBrush;
            body.BorderThickness = new Thickness(2);

            body.CornerRadius = new CornerRadius(9);

            body.Cursor = Cursors.SizeAll;

            StackPanel content = new StackPanel();

            content.Orientation = Orientation.Vertical;
            content.HorizontalAlignment = HorizontalAlignment.Center;
            content.VerticalAlignment = VerticalAlignment.Center;

            TextBlock title = new TextBlock();

            title.Text = component.Title +
                " #" + component.Id;

            title.FontSize = 13;
            title.FontWeight = FontWeights.Bold;
            title.Foreground = Brushes.White;
            title.TextAlignment = TextAlignment.Center;

            title.TextWrapping = TextWrapping.Wrap;

            title.MaxWidth = component.Width - 35;

            title.Margin = new Thickness(
                0,
                0,
                0,
                9
            );

            content.Children.Add(title);

            TextBlock detail = new TextBlock();

            detail.Text = "";
            detail.FontSize = 12;
            detail.Foreground = Brushes.White;

            detail.TextAlignment = TextAlignment.Center;
            detail.TextWrapping = TextWrapping.Wrap;

            detail.MaxWidth = component.Width - 75;

            content.Children.Add(detail);

            if (component.Kind == ComponentKind.Led)
            {
                Ellipse indicator = new Ellipse();

                indicator.Width = 36;
                indicator.Height = 36;

                indicator.Fill = Brushes.DarkRed;
                indicator.Stroke = Brushes.White;
                indicator.StrokeThickness = 2;

                indicator.HorizontalAlignment =
                    HorizontalAlignment.Center;

                indicator.Margin = new Thickness(
                    0,
                    10,
                    0,
                    0
                );

                content.Children.Add(indicator);

                view.LedIndicator = indicator;
            }

            body.Child = content;

            view.Body = body;
            view.Title = title;
            view.Detail = detail;

            body.MouseLeftButtonDown +=
                delegate(object sender, MouseButtonEventArgs e)
                {
                    SelectComponent(component);

                    if (e.ClickCount == 2 &&
                        component.IsManualSource)
                    {
                        circuit.Toggle(component);

                        RefreshAll();

                        SetStatus(
                            "Estado de " +
                            component.Title +
                            " alterado."
                        );
                    }

                    draggingComponent = component;

                    dragStart = e.GetPosition(BoardSurface);

                    dragOriginalX = component.X;
                    dragOriginalY = component.Y;

                    body.CaptureMouse();

                    e.Handled = true;
                };

            body.MouseMove +=
                delegate(object sender, MouseEventArgs e)
                {
                    MoveComponent(
                        component,
                        e
                    );
                };

            body.MouseLeftButtonUp +=
                delegate(object sender, MouseButtonEventArgs e)
                {
                    if (draggingComponent == component)
                    {
                        draggingComponent = null;

                        body.ReleaseMouseCapture();

                        e.Handled = true;
                    }
                };

            body.LostMouseCapture +=
                delegate(object sender, MouseEventArgs e)
                {
                    if (draggingComponent == component)
                    {
                        draggingComponent = null;
                    }
                };

            NodeCanvas.Children.Add(body);

            for (int i = 0; i < component.InputCount; i++)
            {
                int pinIndex = i;

                Ellipse pin = CreatePin();

                pin.ToolTip =
                    "Entrada " + component.InputName(i);

                pin.MouseLeftButtonDown +=
                    delegate(
                        object sender,
                        MouseButtonEventArgs e
                    )
                    {
                        ConnectToInput(
                            component,
                            pinIndex
                        );

                        e.Handled = true;
                    };

                view.InputPins.Add(pin);

                NodeCanvas.Children.Add(pin);

                TextBlock label = CreatePinLabel(
                    component.InputName(i)
                );

                view.InputLabels.Add(label);

                NodeCanvas.Children.Add(label);
            }

            for (int i = 0; i < component.OutputCount; i++)
            {
                int pinIndex = i;

                Ellipse pin = CreatePin();

                pin.ToolTip =
                    "Saída " + component.OutputName(i);

                pin.MouseLeftButtonDown +=
                    delegate(
                        object sender,
                        MouseButtonEventArgs e
                    )
                    {
                        BeginConnection(
                            component,
                            pinIndex
                        );

                        e.Handled = true;
                    };

                view.OutputPins.Add(pin);

                NodeCanvas.Children.Add(pin);

                TextBlock label = CreatePinLabel(
                    component.OutputName(i)
                );

                view.OutputLabels.Add(label);

                NodeCanvas.Children.Add(label);
            }

            visuals.Add(component, view);

            PositionComponentVisual(view);

            RefreshComponentVisual(view);
        }

        private Ellipse CreatePin()
        {
            Ellipse pin = new Ellipse();

            pin.Width = 15;
            pin.Height = 15;

            pin.Fill = LowSignalBrush;
            pin.Stroke = Brushes.White;
            pin.StrokeThickness = 1.5;

            pin.Cursor = Cursors.Hand;

            Canvas.SetZIndex(pin, 4);

            return pin;
        }

        private TextBlock CreatePinLabel(string name)
        {
            TextBlock label = new TextBlock();

            label.Text = name;
            label.FontSize = 10;
            label.FontWeight = FontWeights.Bold;
            label.Foreground = Brushes.LightGray;

            label.IsHitTestVisible = false;

            Canvas.SetZIndex(label, 3);

            return label;
        }

        private void PositionComponentVisual(
            ComponentVisual view
        )
        {
            CircuitComponent component = view.Component;

            Canvas.SetLeft(
                view.Body,
                component.X
            );

            Canvas.SetTop(
                view.Body,
                component.Y
            );

            for (int i = 0; i < view.InputPins.Count; i++)
            {
                double y = component.Y +
                    PinTop + i * PinSpacing;

                Canvas.SetLeft(
                    view.InputPins[i],
                    component.X - 7.5
                );

                Canvas.SetTop(
                    view.InputPins[i],
                    y - 7.5
                );

                Canvas.SetLeft(
                    view.InputLabels[i],
                    component.X + 15
                );

                Canvas.SetTop(
                    view.InputLabels[i],
                    y - 8
                );
            }

            for (int i = 0; i < view.OutputPins.Count; i++)
            {
                double y = component.Y +
                    PinTop + i * PinSpacing;

                Canvas.SetLeft(
                    view.OutputPins[i],
                    component.X + component.Width - 7.5
                );

                Canvas.SetTop(
                    view.OutputPins[i],
                    y - 7.5
                );

                Canvas.SetLeft(
                    view.OutputLabels[i],
                    component.X + component.Width - 43
                );

                Canvas.SetTop(
                    view.OutputLabels[i],
                    y - 8
                );
            }
        }

        private void MoveComponent(
            CircuitComponent component,
            MouseEventArgs e
        )
        {
            if (draggingComponent != component)
            {
                return;
            }

            if (e.LeftButton != MouseButtonState.Pressed)
            {
                return;
            }

            Point current = e.GetPosition(BoardSurface);

            double differenceX = current.X - dragStart.X;
            double differenceY = current.Y - dragStart.Y;

            double newX = dragOriginalX + differenceX;
            double newY = dragOriginalY + differenceY;

            newX = Math.Max(
                12,
                Math.Min(
                    newX,
                    BoardSurface.Width -
                    component.Width - 12
                )
            );

            newY = Math.Max(
                12,
                Math.Min(
                    newY,
                    BoardSurface.Height -
                    component.Height - 12
                )
            );

            component.X = newX;
            component.Y = newY;

            PositionComponentVisual(
                visuals[component]
            );

            DrawWires();

            e.Handled = true;
        }

        private void BeginConnection(
            CircuitComponent component,
            int pin
        )
        {
            SelectComponent(component);

            pendingSource = component;
            pendingSourcePin = pin;

            RefreshAll();

            SetStatus(
                "Saída " + component.OutputName(pin) +
                " selecionada. Clique em uma entrada " +
                "para criar a ligação."
            );
        }

        private void ConnectToInput(
            CircuitComponent component,
            int pin
        )
        {
            SelectComponent(component);

            if (pendingSource == null)
            {
                SetStatus(
                    "Selecione primeiro um terminal de saída."
                );

                return;
            }

            try
            {
                circuit.Connect(
                    pendingSource,
                    pendingSourcePin,
                    component,
                    pin
                );

                SetStatus(
                    "Fio conectado à entrada " +
                    component.InputName(pin) +
                    " do componente #" +
                    component.Id + "."
                );
            }
            catch (InvalidOperationException exception)
            {
                SetStatus(
                    "Ligação rejeitada: " +
                    exception.Message
                );
            }

            pendingSource = null;

            RefreshAll();
        }

        private void DrawWires()
        {
            WireCanvas.Children.Clear();

            foreach (CircuitWire wire in circuit.Wires)
            {
                double sourceX =
                    wire.Source.X + wire.Source.Width;

                double sourceY =
                    wire.Source.Y +
                    PinTop + wire.SourcePin * PinSpacing;

                double targetX = wire.Target.X;

                double targetY =
                    wire.Target.Y +
                    PinTop + wire.TargetPin * PinSpacing;

                double bendX;

                if (targetX > sourceX + 35)
                {
                    bendX = (sourceX + targetX) / 2;
                }
                else
                {
                    bendX = sourceX + 35;
                }

                bool value = wire.Source.OutputStates[
                    wire.SourcePin
                ];

                Polyline drawing = new Polyline();

                drawing.Stroke = value
                    ? HighSignalBrush
                    : LowSignalBrush;

                drawing.StrokeThickness = 4;

                drawing.StrokeLineJoin =
                    PenLineJoin.Round;

                drawing.StrokeStartLineCap =
                    PenLineCap.Round;

                drawing.StrokeEndLineCap =
                    PenLineCap.Round;

                PointCollection points =
                    new PointCollection();

                points.Add(
                    new Point(sourceX, sourceY)
                );

                points.Add(
                    new Point(bendX, sourceY)
                );

                points.Add(
                    new Point(bendX, targetY)
                );

                points.Add(
                    new Point(targetX, targetY)
                );

                drawing.Points = points;

                drawing.Cursor = Cursors.Hand;

                drawing.ToolTip =
                    "Fio: #" + wire.Source.Id +
                    " (" +
                    wire.Source.OutputName(wire.SourcePin) +
                    ") para #" + wire.Target.Id +
                    " (" +
                    wire.Target.InputName(wire.TargetPin) +
                    ")\nClique com o botão direito para remover.";

                CircuitWire selectedWire = wire;

                drawing.MouseRightButtonDown +=
                    delegate(
                        object sender,
                        MouseButtonEventArgs e
                    )
                    {
                        circuit.RemoveWire(
                            selectedWire
                        );

                        RefreshAll();

                        SetStatus(
                            "Fio removido."
                        );

                        e.Handled = true;
                    };

                WireCanvas.Children.Add(drawing);
            }
        }

        private void SelectComponent(
            CircuitComponent component
        )
        {
            selectedComponent = component;

            if (component != null &&
                component.Kind == ComponentKind.Keypad8)
            {
                numericDraft =
                    component.NumericValue.ToString(
                        CultureInfo.InvariantCulture
                    );

                KeypadNumberText.Text = numericDraft;
            }
            else
            {
                numericDraft = "";
            }

            RefreshComponentStyles();

            RefreshInspector();
        }

        private void RefreshComponentStyles()
        {
            foreach (ComponentVisual view in visuals.Values)
            {
                view.Body.BorderBrush =
                    view.Component == selectedComponent
                        ? SelectedBrush
                        : ComponentBorderBrush;

                view.Body.BorderThickness =
                    view.Component == selectedComponent
                        ? new Thickness(3)
                        : new Thickness(2);
            }
        }

        private void RefreshComponentVisual(
            ComponentVisual view
        )
        {
            CircuitComponent component = view.Component;

            for (int i = 0; i < view.InputPins.Count; i++)
            {
                bool connected =
                    circuit.IsInputConnected(
                        component,
                        i
                    );

                if (!connected)
                {
                    view.InputPins[i].Fill =
                        Brushes.DarkSlateGray;
                }
                else
                {
                    view.InputPins[i].Fill =
                        circuit.ReadInput(component, i)
                            ? HighSignalBrush
                            : LowSignalBrush;
                }
            }

            for (int i = 0; i < view.OutputPins.Count; i++)
            {
                bool active = component.OutputStates[i];

                view.OutputPins[i].Fill =
                    active
                        ? HighSignalBrush
                        : LowSignalBrush;

                if (pendingSource == component &&
                    pendingSourcePin == i)
                {
                    view.OutputPins[i].Stroke =
                        Brushes.Gold;

                    view.OutputPins[i].StrokeThickness = 3;
                }
                else
                {
                    view.OutputPins[i].Stroke =
                        Brushes.White;

                    view.OutputPins[i].StrokeThickness = 1.5;
                }
            }

            view.Detail.Text = ComponentDescription(
                component
            );

            if (component.Kind == ComponentKind.Led)
            {
                bool on = circuit.ReadIndicator(
                    component
                );

                view.LedIndicator.Fill = on
                    ? HighSignalBrush
                    : Brushes.DarkRed;

                view.Body.Background = on
                    ? new SolidColorBrush(
                        Color.FromRgb(25, 68, 49)
                    )
                    : ComponentBrush;
            }
            else
            {
                view.Body.Background = ComponentBrush;
            }
        }

        private string ComponentDescription(
            CircuitComponent component
        )
        {
            switch (component.Kind)
            {
                case ComponentKind.High:
                    return "Sinal fixo: 1";

                case ComponentKind.Input:
                    return "Entrada: " +
                        Bit(component.ToggleValue) +
                        "\nDuplo clique para alternar";

                case ComponentKind.Switch:
                    return component.ToggleValue
                        ? "LIGADO\nDuplo clique para alternar"
                        : "DESLIGADO\nDuplo clique para alternar";

                case ComponentKind.Clock:
                    return "Clock: " +
                        Bit(component.ClockValue);

                case ComponentKind.And:
                case ComponentKind.Or:
                case ComponentKind.Not:
                case ComponentKind.Junction:
                    return "Saída: " +
                        Bit(component.OutputStates[0]);

                case ComponentKind.Output:
                    return "Sinal recebido: " +
                        Bit(circuit.ReadIndicator(component));

                case ComponentKind.Led:
                    return circuit.ReadIndicator(component)
                        ? "LIGADO"
                        : "DESLIGADO";

                case ComponentKind.Display8:
                    byte displayValue =
                        circuit.ReadDisplay(component);

                    return "DEC: " +
                        displayValue.ToString(
                            CultureInfo.InvariantCulture
                        ) +
                        "\nHEX: 0x" +
                        displayValue.ToString("X2") +
                        "\nBIN: " +
                        Binary8(displayValue);

                case ComponentKind.Keypad8:
                    return "DEC: " +
                        component.NumericValue.ToString(
                            CultureInfo.InvariantCulture
                        ) +
                        "\nHEX: 0x" +
                        component.NumericValue.ToString("X2") +
                        "\nBIN: " +
                        Binary8(component.NumericValue);

                case ComponentKind.FlipFlopD:
                    return "Q: " +
                        Bit(component.StoredValue) +
                        "\nCLK anterior: " +
                        Bit(component.PreviousClock);

                case ComponentKind.FlipFlopJK:
                    return "Q: " +
                        Bit(component.StoredValue) +
                        "\nCLK anterior: " +
                        Bit(component.PreviousClock);

                default:
                    return "";
            }
        }

        private string Bit(bool value)
        {
            return value ? "1" : "0";
        }

        private string Binary8(byte value)
        {
            return Convert.ToString(value, 2).PadLeft(
                8,
                '0'
            );
        }

        private void RefreshInspector()
        {
            if (selectedComponent == null)
            {
                InspectorText.Text =
                    "Nenhum componente selecionado.\n\n" +
                    "COMO MONTAR UM CIRCUITO\n\n" +
                    "1. Insira os componentes pela paleta.\n\n" +
                    "2. Arraste os componentes para posicioná-los.\n\n" +
                    "3. Clique em um pino de saída à direita.\n\n" +
                    "4. Clique em um pino de entrada à esquerda.\n\n" +
                    "5. Execute ou avance a simulação.\n\n" +
                    "6. Clique com o botão direito sobre um " +
                    "fio para removê-lo.\n\n" +
                    "Fios verdes representam nível 1.\n" +
                    "Fios cinzas representam nível 0.\n\n" +
                    "Os pinos b0 a b7 representam bits do " +
                    "menos significativo ao mais significativo.";

                return;
            }

            CircuitComponent component = selectedComponent;

            StringBuilder builder = new StringBuilder();

            builder.AppendLine(
                component.Title + " #" + component.Id
            );

            builder.AppendLine();

            builder.AppendLine(
                "Posição: X = " +
                ((int)component.X) +
                ", Y = " +
                ((int)component.Y)
            );

            builder.AppendLine();

            builder.AppendLine(
                "Entradas: " + component.InputCount
            );

            for (int i = 0; i < component.InputCount; i++)
            {
                builder.AppendLine(
                    "  " +
                    component.InputName(i) +
                    " = " +
                    Bit(circuit.ReadInput(component, i)) +
                    (
                        circuit.IsInputConnected(component, i)
                        ? " (conectada)"
                        : " (sem fio)"
                    )
                );
            }

            builder.AppendLine();

            builder.AppendLine(
                "Saídas: " + component.OutputCount
            );

            for (int i = 0; i < component.OutputCount; i++)
            {
                builder.AppendLine(
                    "  " +
                    component.OutputName(i) +
                    " = " +
                    Bit(component.OutputStates[i])
                );
            }

            builder.AppendLine();

            builder.AppendLine(
                "ESTADO"
            );

            builder.AppendLine(
                ComponentDescription(component)
            );

            if (component.IsManualSource)
            {
                builder.AppendLine();

                builder.AppendLine(
                    "Use duplo clique ou o botão de alternância."
                );
            }

            if (component.Kind == ComponentKind.Keypad8)
            {
                builder.AppendLine();

                builder.AppendLine(
                    "Digite um valor de 0 a 255 no campo " +
                    "inferior e clique em Aplicar valor."
                );

                builder.AppendLine();

                builder.AppendLine(
                    "Também é possível digitar os algarismos " +
                    "diretamente após selecionar o componente " +
                    "e pressionar Enter."
                );
            }

            if (component.Kind == ComponentKind.FlipFlopD ||
                component.Kind == ComponentKind.FlipFlopJK)
            {
                builder.AppendLine();

                builder.AppendLine(
                    "A memória é atualizada na borda de " +
                    "subida do sinal de clock, durante um passo."
                );
            }

            InspectorText.Text = builder.ToString();
        }

        private void RefreshAll()
        {
            foreach (ComponentVisual view in visuals.Values)
            {
                RefreshComponentVisual(view);
            }

            RefreshComponentStyles();

            DrawWires();

            RefreshInspector();

            if (!circuit.LastPropagationConverged)
            {
                SetStatus(
                    "Atenção: a propagação combinacional " +
                    "não convergiu dentro do limite de " +
                    "iterações. Verifique realimentações no circuito."
                );
            }
        }

        private void ToggleSelected_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            if (selectedComponent == null)
            {
                SetStatus(
                    "Selecione uma entrada lógica ou interruptor."
                );

                return;
            }

            try
            {
                circuit.Toggle(selectedComponent);

                RefreshAll();

                SetStatus(
                    "Estado lógico alterado."
                );
            }
            catch (InvalidOperationException exception)
            {
                SetStatus(exception.Message);
            }
        }

        private void RemoveSelected_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            if (selectedComponent == null)
            {
                SetStatus(
                    "Nenhum componente selecionado."
                );

                return;
            }

            CircuitComponent removed = selectedComponent;

            if (pendingSource == removed)
            {
                pendingSource = null;
            }

            ComponentVisual view = visuals[removed];

            NodeCanvas.Children.Remove(view.Body);

            foreach (Ellipse pin in view.InputPins)
            {
                NodeCanvas.Children.Remove(pin);
            }

            foreach (Ellipse pin in view.OutputPins)
            {
                NodeCanvas.Children.Remove(pin);
            }

            foreach (TextBlock label in view.InputLabels)
            {
                NodeCanvas.Children.Remove(label);
            }

            foreach (TextBlock label in view.OutputLabels)
            {
                NodeCanvas.Children.Remove(label);
            }

            visuals.Remove(removed);

            circuit.Remove(removed);

            selectedComponent = null;

            RefreshAll();

            SetStatus(
                "Componente removido. " +
                "As conexões relacionadas também foram removidas."
            );
        }

        private void CancelConnection_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            pendingSource = null;

            RefreshAll();

            SetStatus(
                "Seleção de conexão cancelada."
            );
        }

        private void ApplyNumber_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            ApplyNumericValue();
        }

        private void ApplyNumericValue()
        {
            if (selectedComponent == null ||
                selectedComponent.Kind != ComponentKind.Keypad8)
            {
                SetStatus(
                    "Selecione um teclado numérico de oito bits."
                );

                return;
            }

            byte value;

            bool valid = byte.TryParse(
                KeypadNumberText.Text.Trim(),
                NumberStyles.Integer,
                CultureInfo.InvariantCulture,
                out value
            );

            if (!valid)
            {
                SetStatus(
                    "Valor inválido. Informe um inteiro " +
                    "entre 0 e 255."
                );

                return;
            }

            circuit.SetNumericValue(
                selectedComponent,
                value
            );

            numericDraft = value.ToString(
                CultureInfo.InvariantCulture
            );

            KeypadNumberText.Text = numericDraft;

            RefreshAll();

            SetStatus(
                "Teclado numérico atualizado para " +
                numericDraft + "."
            );
        }

        private void Window_PreviewKeyDown(
            object sender,
            KeyEventArgs e
        )
        {
            if (selectedComponent == null ||
                selectedComponent.Kind != ComponentKind.Keypad8)
            {
                return;
            }

            if (Keyboard.FocusedElement is TextBox)
            {
                return;
            }

            int digit = GetDigit(e.Key);

            if (digit >= 0)
            {
                if (numericDraft.Length >= 3)
                {
                    numericDraft = digit.ToString(
                        CultureInfo.InvariantCulture
                    );
                }
                else
                {
                    numericDraft += digit.ToString(
                        CultureInfo.InvariantCulture
                    );
                }

                KeypadNumberText.Text = numericDraft;

                e.Handled = true;

                return;
            }

            if (e.Key == Key.Back)
            {
                if (numericDraft.Length > 0)
                {
                    numericDraft = numericDraft.Substring(
                        0,
                        numericDraft.Length - 1
                    );
                }

                KeypadNumberText.Text = numericDraft;

                e.Handled = true;

                return;
            }

            if (e.Key == Key.Enter ||
                e.Key == Key.Return)
            {
                ApplyNumericValue();

                e.Handled = true;

                return;
            }

            if (e.Key == Key.Escape)
            {
                numericDraft =
                    selectedComponent.NumericValue.ToString(
                        CultureInfo.InvariantCulture
                    );

                KeypadNumberText.Text = numericDraft;

                e.Handled = true;
            }
        }

        private int GetDigit(Key key)
        {
            if (key >= Key.D0 && key <= Key.D9)
            {
                return (int)key - (int)Key.D0;
            }

            if (key >= Key.NumPad0 &&
                key <= Key.NumPad9)
            {
                return (int)key - (int)Key.NumPad0;
            }

            return -1;
        }

        private void RunButton_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            running = true;
            paused = false;

            simulationTimer.Start();

            UpdateSimulationState();

            SetStatus(
                "Simulação contínua iniciada."
            );
        }

        private void PauseButton_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            if (!running)
            {
                running = true;
                paused = false;

                simulationTimer.Start();

                SetStatus(
                    "Simulação iniciada."
                );
            }
            else
            {
                paused = !paused;

                if (paused)
                {
                    simulationTimer.Stop();

                    SetStatus(
                        "Simulação pausada."
                    );
                }
                else
                {
                    simulationTimer.Start();

                    SetStatus(
                        "Simulação retomada."
                    );
                }
            }

            UpdateSimulationState();
        }

        private void StepButton_Click(
            object sender,
            RoutedEventArgs e
        )
        {
            simulationTimer.Stop();

            running = false;
            paused = false;

            AdvanceSimulation();

            UpdateSimulationState();

            SetStatus(
                "Um passo da simulação foi executado."
            );
        }

        private void SimulationTimer_Tick(
            object sender,
            EventArgs e
        )
        {
            AdvanceSimulation();
        }

        private void AdvanceSimulation()
        {
            circuit.AdvanceStep();

            RefreshAll();
        }

        private void IntervalSlider_ValueChanged(
            object sender,
            RoutedPropertyChangedEventArgs<double> e
        )
        {
            if (IntervalSlider == null ||
                IntervalText == null)
            {
                return;
            }

            int interval = (int)IntervalSlider.Value;

            IntervalText.Text = interval + " ms";

            if (simulationTimer != null)
            {
                simulationTimer.Interval =
                    TimeSpan.FromMilliseconds(interval);
            }
        }

        private void UpdateSimulationState()
        {
            if (!running)
            {
                SimulationStateText.Text = "Parado";
                SimulationStateText.Foreground =
                    Brushes.Goldenrod;
            }
            else if (paused)
            {
                SimulationStateText.Text = "Pausado";
                SimulationStateText.Foreground =
                    Brushes.Orange;
            }
            else
            {
                SimulationStateText.Text = "Executando";
                SimulationStateText.Foreground =
                    HighSignalBrush;
            }
        }

        private void SetStatus(string message)
        {
            StatusText.Text = message;
        }
    }
}